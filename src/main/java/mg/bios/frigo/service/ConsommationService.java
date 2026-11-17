package mg.bios.frigo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mg.bios.frigo.dto.ConsommationDto;
import mg.bios.frigo.dto.LotModifieDto;
import mg.bios.frigo.entity.AlimentEnStock;
import mg.bios.frigo.entity.DictionnaireAliment;
import mg.bios.frigo.entity.HistoriqueConsommation;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.AlimentEnStockRepository;
import mg.bios.frigo.repository.DictionnaireAlimentRepository;
import mg.bios.frigo.repository.HistoriqueConsommationRepository;
import mg.bios.frigo.repository.OuverturePorteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsommationService {

    private final DictionnaireAlimentRepository dictionnaireRepository;
    private final AlimentEnStockRepository stockRepository;
    private final HistoriqueConsommationRepository historiqueRepository;
    private final CourseService courseService;
    private final OuverturePorteRepository porteRepository;
    private final DiffusionService diffusion;

    public ConsommationService(DictionnaireAlimentRepository dictionnaireRepository,
                               AlimentEnStockRepository stockRepository,
                               HistoriqueConsommationRepository historiqueRepository,
                               CourseService courseService,
                               OuverturePorteRepository porteRepository,
                               DiffusionService diffusion) {
        this.dictionnaireRepository = dictionnaireRepository;
        this.stockRepository = stockRepository;
        this.historiqueRepository = historiqueRepository;
        this.courseService = courseService;
        this.porteRepository = porteRepository;
        this.diffusion = diffusion;
    }

    @Transactional
    public ConsommationDto consommer(Integer idAliment, Integer quantite, Utilisateur utilisateur) {
        // 1. La quantité doit être un entier positif
        if (quantite == null || quantite <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "QUANTITE_INVALIDE",
                    "La quantité doit être un entier supérieur à 0");
        }

        // 2. L'aliment doit exister dans le dictionnaire
        DictionnaireAliment aliment = dictionnaireRepository.findById(idAliment)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ALIMENT_INCONNU",
                        "Aliment inconnu : " + idAliment));

        // 3. La porte doit être ouverte (R12)
        if (porteRepository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc().isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTE_FERMEE",
                    "Impossible de prendre un aliment : la porte est fermée");
        }

        // 4. Les lots consommables : non périmés, le plus proche de la péremption d'abord
        LocalDate aujourdhui = LocalDate.now();
        List<AlimentEnStock> lots = stockRepository.findLotsConsommables(idAliment, aujourdhui);
        int disponible = lots.stream().mapToInt(AlimentEnStock::getQuantite).sum();
        if (quantite > disponible) {
            throw new ApiException(HttpStatus.CONFLICT, "STOCK_INSUFFISANT",
                    "Stock insuffisant : " + disponible + " disponible(s)",
                    Map.of("stockDisponible", disponible));
        }

        // 5. Retirer, lot après lot (le lot vide reste en base : la rupture reste visible)
        int restant = quantite;
        List<LotModifieDto> lotsTouches = new ArrayList<>();
        for (AlimentEnStock lot : lots) {
            if (restant == 0) {
                break;
            }
            int aRetirer = Math.min(restant, lot.getQuantite());
            lot.retirer(aRetirer);
            restant -= aRetirer;
            lotsTouches.add(new LotModifieDto(lot.getId(), lot.getQuantite()));
        }

        // 6. Écrire l'historique
        HistoriqueConsommation prise = new HistoriqueConsommation();
        prise.setUtilisateur(utilisateur);
        prise.setDictionnaire(aliment);
        prise.setQuantitePrise(quantite);
        historiqueRepository.save(prise);

        // 7. Liste de courses (R5)
        int nouveauDisponible = disponible - quantite;
        boolean ajouteAuxCourses = courseService.synchroniser(aliment, nouveauDisponible);

        // 8. Événements, diffusés après validation de la transaction
        diffusion.evenement("/topic/stocks", "STOCK_MODIFIE");
        if (ajouteAuxCourses) {
            diffusion.evenement("/topic/courses", "COURSES_MODIFIEES");
        }

        return new ConsommationDto(
                aliment.getId(),
                aliment.getNom(),
                quantite,
                nouveauDisponible,
                aliment.getSeuilMinimum(),
                StockService.calculerStatut(nouveauDisponible, aliment.getSeuilMinimum()),
                ajouteAuxCourses,
                lotsTouches);
    }
}