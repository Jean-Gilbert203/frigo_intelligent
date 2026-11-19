package mg.bios.frigo.service;

import java.util.List;
import mg.bios.frigo.dto.AvanceeTempsDto;
import mg.bios.frigo.dto.ResultatSurveillance;
import mg.bios.frigo.entity.AlimentEnStock;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.AlimentEnStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulationService {

    private final AlimentEnStockRepository stockRepository;
    private final SurveillanceService surveillance;
    private final DiffusionService diffusion;

    public SimulationService(AlimentEnStockRepository stockRepository,
                             SurveillanceService surveillance,
                             DiffusionService diffusion) {
        this.stockRepository = stockRepository;
        this.surveillance = surveillance;
        this.diffusion = diffusion;
    }

    @Transactional
    public AvanceeTempsDto avancerTemps(Integer jours) {
        if (jours == null || jours <= 0 || jours > 3650) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "JOURS_INVALIDES",
                    "Le nombre de jours doit être compris entre 1 et 3650");
        }

        // Retirer X jours à toutes les dates de péremption
        List<AlimentEnStock> lots = stockRepository.findAll();
        for (AlimentEnStock lot : lots) {
            lot.setDatePeremption(lot.getDatePeremption().minusDays(jours));
        }
        stockRepository.saveAll(lots);

        // Lancer immédiatement les contrôles du jour
        ResultatSurveillance resultat = surveillance.verifier();

        // Un seul événement, pas un par lot
        diffusion.evenement("/topic/stocks", "STOCK_MODIFIE");
        if (resultat.coursesModifiees()) {
            diffusion.evenement("/topic/courses", "COURSES_MODIFIEES");
        }

        return new AvanceeTempsDto(jours, lots.size(), resultat.notificationsCreees());
    }
}