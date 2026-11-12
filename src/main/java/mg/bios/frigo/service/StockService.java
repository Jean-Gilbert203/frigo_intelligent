package mg.bios.frigo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mg.bios.frigo.dto.LotStockDto;
import mg.bios.frigo.dto.StockAlimentDto;
import mg.bios.frigo.dto.StockCompartimentDto;
import mg.bios.frigo.entity.AlimentEnStock;
import mg.bios.frigo.entity.Compartiment;
import mg.bios.frigo.entity.DictionnaireAliment;
import mg.bios.frigo.repository.AlimentEnStockRepository;
import mg.bios.frigo.repository.CompartimentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final AlimentEnStockRepository stockRepository;
    private final CompartimentRepository compartimentRepository;
    private final int joursAlertePeremption;

    // Le :3 est la valeur par défaut si la ligne manque dans application.properties
    public StockService(AlimentEnStockRepository stockRepository,
                        CompartimentRepository compartimentRepository,
                        @Value("${frigo.alerte-peremption-jours:3}") int joursAlertePeremption) {
        this.stockRepository = stockRepository;
        this.compartimentRepository = compartimentRepository;
        this.joursAlertePeremption = joursAlertePeremption;
    }

    public List<StockCompartimentDto> lireStocks() {
        LocalDate aujourdhui = LocalDate.now();
        List<AlimentEnStock> lots = stockRepository.findAllAvecDetails();

        // Stock total de chaque aliment, tous compartiments confondus
        Map<Integer, Integer> totalParAliment = new HashMap<>();
        for (AlimentEnStock lot : lots) {
            totalParAliment.merge(lot.getDictionnaire().getId(), lot.getQuantite(), Integer::sum);
        }

        // Un bloc par compartiment (même les compartiments vides)
        List<StockCompartimentDto> resultat = new ArrayList<>();
        for (Compartiment compartiment : compartimentRepository.findAll(Sort.by("id"))) {

            // Les lots de ce compartiment, regroupés par aliment
            Map<Integer, List<AlimentEnStock>> lotsParAliment = new LinkedHashMap<>();
            int remplissage = 0;
            for (AlimentEnStock lot : lots) {
                if (lot.getCompartiment().getId().equals(compartiment.getId())) {
                    lotsParAliment
                            .computeIfAbsent(lot.getDictionnaire().getId(), cle -> new ArrayList<>())
                            .add(lot);
                    remplissage += lot.getQuantite();
                }
            }

            // Un bloc par aliment, avec ses lots
            List<StockAlimentDto> aliments = new ArrayList<>();
            for (List<AlimentEnStock> groupe : lotsParAliment.values()) {
                DictionnaireAliment aliment = groupe.get(0).getDictionnaire();
                int total = totalParAliment.get(aliment.getId());
                int quantiteIci = groupe.stream().mapToInt(AlimentEnStock::getQuantite).sum();
                List<LotStockDto> lotsDto = groupe.stream()
                        .map(lot -> versLotDto(lot, aujourdhui))
                        .toList();
                aliments.add(new StockAlimentDto(
                        aliment.getId(),
                        aliment.getNom(),
                        aliment.getCategorie().getNom(),
                        "/images/aliments/" + aliment.getImage(),
                        aliment.getSeuilMinimum(),
                        quantiteIci,
                        total,
                        calculerStatut(total, aliment.getSeuilMinimum()),
                        lotsDto));
            }

            resultat.add(new StockCompartimentDto(
                    compartiment.getId(),
                    compartiment.getNom(),
                    compartiment.getTemperatureActuelle(),
                    compartiment.getCapaciteMax(),
                    remplissage,
                    aliments));
        }
        return resultat;
    }

    // LA règle de péremption, écrite une seule fois : les autres classes
    // (tâche planifiée, alertes) appelleront cette méthode au lieu de la recopier
    public String etatPeremption(long joursRestants) {
        if (joursRestants < 0) {
            return "PERIME";
        }
        if (joursRestants <= joursAlertePeremption) {
            return "BIENTOT_PERIME";
        }
        return "OK";
    }

    private LotStockDto versLotDto(AlimentEnStock lot, LocalDate aujourdhui) {
        long jours = ChronoUnit.DAYS.between(aujourdhui, lot.getDatePeremption());
        return new LotStockDto(lot.getId(), lot.getQuantite(), lot.getDateAjout(),
                lot.getDatePeremption(), jours, etatPeremption(jours));
    }

    // Statut de l'aliment d'après son stock total : RUPTURE, STOCK_BAS ou OK
    private String calculerStatut(int total, int seuil) {
        if (total == 0) {
            return "RUPTURE";
        }
        if (total <= seuil) {
            return "STOCK_BAS";
        }
        return "OK";
    }
}