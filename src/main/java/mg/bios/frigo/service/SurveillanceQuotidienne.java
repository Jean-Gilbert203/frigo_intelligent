package mg.bios.frigo.service;

import mg.bios.frigo.dto.ResultatSurveillance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Lance chaque jour les contrôles de stock, de courses et de péremption
@Component
public class SurveillanceQuotidienne {

    private static final Logger LOG = LoggerFactory.getLogger(SurveillanceQuotidienne.class);

    private final SurveillanceService surveillance;
    private final DiffusionService diffusion;

    public SurveillanceQuotidienne(SurveillanceService surveillance, DiffusionService diffusion) {
        this.surveillance = surveillance;
        this.diffusion = diffusion;
    }

    @Scheduled(cron = "${frigo.surveillance.cron:0 0 7 * * *}", zone = "Indian/Antananarivo")
    public void executer() {
        ResultatSurveillance resultat = surveillance.verifier();
        LOG.info("Surveillance planifiée : {} notification(s) créée(s), courses modifiées : {}",
                resultat.notificationsCreees(), resultat.coursesModifiees());

        // Les jours qui passent changent l'état des lots 
        diffusion.evenement("/topic/stocks", "STOCK_MODIFIE");
        if (resultat.coursesModifiees()) {
            diffusion.evenement("/topic/courses", "COURSES_MODIFIEES");
        }
    }
}