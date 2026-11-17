package mg.bios.frigo.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import mg.bios.frigo.dto.AjoutDto;
import mg.bios.frigo.dto.AjoutRequest;
import mg.bios.frigo.dto.LotAjouteDto;
import mg.bios.frigo.entity.AlimentEnStock;
import mg.bios.frigo.entity.Compartiment;
import mg.bios.frigo.entity.DictionnaireAliment;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.AlimentEnStockRepository;
import mg.bios.frigo.repository.CompartimentRepository;
import mg.bios.frigo.repository.DictionnaireAlimentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AjoutService {

    private final DictionnaireAlimentRepository dictionnaireRepository;
    private final CompartimentRepository compartimentRepository;
    private final AlimentEnStockRepository stockRepository;
    private final CourseService courseService;
    private final DiffusionService diffusion;

    public AjoutService(DictionnaireAlimentRepository dictionnaireRepository,
                        CompartimentRepository compartimentRepository,
                        AlimentEnStockRepository stockRepository,
                        CourseService courseService,
                        DiffusionService diffusion) {
        this.dictionnaireRepository = dictionnaireRepository;
        this.compartimentRepository = compartimentRepository;
        this.stockRepository = stockRepository;
        this.courseService = courseService;
        this.diffusion = diffusion;
    }

    @Transactional
    public AjoutDto ajouter(AjoutRequest requete) {
        LocalDate aujourdhui = LocalDate.now();

        // 1. Quantité : entier supérieur à 0
        Integer quantite = requete.quantite();
        if (quantite == null || quantite <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "QUANTITE_INVALIDE",
                    "La quantité doit être un entier supérieur à 0");
        }

        // 2. Date de péremption : présente et pas déjà dépassée
        LocalDate datePeremption = requete.datePeremption();
        if (datePeremption == null || datePeremption.isBefore(aujourdhui)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "DATE_INVALIDE",
                    "La date de péremption est absente ou déjà dépassée");
        }

        // 3. Liste blanche (R1) : l'aliment doit exister dans le dictionnaire
        DictionnaireAliment aliment = trouverAliment(requete);

        // 4. Compartiment : celui de la catégorie par défaut (R2), ou celui choisi
        Compartiment parDefaut = aliment.getCategorie().getCompartimentDefaut();
        Compartiment compartiment = parDefaut;
        String avertissement = null;
        if (requete.idCompartiment() != null) {
            compartiment = compartimentRepository.findById(requete.idCompartiment())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "COMPARTIMENT_INCONNU",
                            "Compartiment inconnu : " + requete.idCompartiment()));
            if (!compartiment.getId().equals(parDefaut.getId())) {
                avertissement = "Ce produit se range normalement dans : " + parDefaut.getNom();
            }
        }

        // 5. Capacité (R3)
        long remplissage = stockRepository.sommeQuantitesCompartiment(compartiment.getId());
        if (remplissage + quantite > compartiment.getCapaciteMax()) {
            throw new ApiException(HttpStatus.CONFLICT, "COMPARTIMENT_PLEIN",
                    compartiment.getNom() + " : capacité dépassée (" + remplissage + "/"
                            + compartiment.getCapaciteMax() + ")",
                    Map.of("compartiment", compartiment.getNom(),
                           "remplissage", remplissage,
                           "capaciteMax", compartiment.getCapaciteMax()));
        }

        // 6. Les lots vides de cet aliment disparaissent (R6)
        stockRepository.deleteByDictionnaireIdAndQuantite(aliment.getId(), 0);

        // 7. Fusion avec un lot identique, ou nouveau lot (R6)
        Optional<AlimentEnStock> identique = stockRepository
                .findFirstByDictionnaireIdAndCompartimentIdAndDatePeremption(
                        aliment.getId(), compartiment.getId(), datePeremption);
        AlimentEnStock lot;
        boolean fusion;
        if (identique.isPresent()) {
            lot = identique.get();
            lot.ajouter(quantite);
            fusion = true;
        } else {
            lot = new AlimentEnStock();
            lot.setDictionnaire(aliment);
            lot.setCompartiment(compartiment);
            lot.setQuantite(quantite);
            lot.setDatePeremption(datePeremption);
            fusion = false;
        }
        lot = stockRepository.save(lot);

        // 8. Liste de courses (R5) : le stock disponible a changé
        int disponible = (int) stockRepository.sommeDisponible(aliment.getId(), aujourdhui);
        boolean coursesModifiees = courseService.synchroniser(aliment, disponible);

        // 9. Événements, diffusés après validation de la transaction
        diffusion.evenement("/topic/stocks", "STOCK_MODIFIE");
        if (coursesModifiees) {
            diffusion.evenement("/topic/courses", "COURSES_MODIFIEES");
        }

        return new AjoutDto(
                new LotAjouteDto(lot.getId(), aliment.getNom(), lot.getQuantite(),
                        lot.getDateAjout(), lot.getDatePeremption(), compartiment.getNom()),
                fusion,
                avertissement);
    }

    // Par identifiant, ou par nom (espaces nettoyés, pluriel simple accepté)
    private DictionnaireAliment trouverAliment(AjoutRequest requete) {
        if (requete.idDictionnaire() != null) {
            return dictionnaireRepository.findById(requete.idDictionnaire())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ALIMENT_INCONNU",
                            "Cet aliment n'est pas dans le dictionnaire"));
        }
        String saisie = requete.nomAliment();
        if (saisie == null || saisie.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ALIMENT_INCONNU",
                    "Indiquer idDictionnaire ou nomAliment");
        }
        String nom = saisie.trim().replaceAll("\\s+", " ");
        Optional<DictionnaireAliment> trouve = dictionnaireRepository.findByNom(nom);
        // Pluriel simple : « Yaourts » donne « Yaourt »
        if (trouve.isEmpty() && nom.length() > 3 && (nom.endsWith("s") || nom.endsWith("x"))) {
            trouve = dictionnaireRepository.findByNom(nom.substring(0, nom.length() - 1));
        }
        return trouve.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ALIMENT_INCONNU",
                "« " + saisie + " » n'est pas dans le dictionnaire"));
    }
}