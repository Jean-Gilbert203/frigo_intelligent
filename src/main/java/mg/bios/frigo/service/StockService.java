package mg.bios.frigo.service;

import java.time.LocalDate;
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

        // 1. Pour chaque aliment : stock total et stock DISPONIBLE (lots périmés exclus)
        Map<Integer, Integer> totalParAliment = new HashMap<>();
        Map<Integer, Integer> disponibleParAliment = new HashMap<>();
        for (AlimentEnStock lot : lots) {
            Integer idAliment = lot.getDictionnaire().getId();
            totalParAliment.merge(idAliment, lot.getQuantite(), Integer::sum);
            if (!lot.estPerime(aujourdhui)) {
                disponibleParAliment.merge(idAliment, lot.getQuantite(), Integer::sum);
            }
        }

        // 2. Un bloc par compartiment (même les compartiments vides)
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

            // 3. Un bloc par aliment, avec ses lots
            List<StockAlimentDto> aliments = new ArrayList<>();
            for (List<AlimentEnStock> groupe : lotsParAliment.values()) {
                DictionnaireAliment aliment = groupe.get(0).getDictionnaire();
                int total = totalParAliment.get(aliment.getId());
                int disponible = disponibleParAliment.getOrDefault(aliment.getId(), 0);
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
                        disponible,
                        calculerStatut(disponible, aliment.getSeuilMinimum()),
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

    // LA règle de péremption, écrite une seule fois
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
        long jours = lot.joursAvantPeremption(aujourdhui);
        return new LotStockDto(lot.getId(), lot.getQuantite(), lot.getDateAjout(),
                lot.getDatePeremption(), jours, etatPeremption(jours));
    }

    // Statut de l'aliment d'après son stock disponible : RUPTURE, STOCK_BAS ou OK
    public static String calculerStatut(int disponible, int seuil) {
        if (disponible == 0) {
            return "RUPTURE";
        }
        if (disponible <= seuil) {
            return "STOCK_BAS";
        }
        return "OK";
    }
}