package mg.bios.frigo.service;

import java.time.Instant;
import java.util.Optional;
import mg.bios.frigo.dto.PorteDto;
import mg.bios.frigo.entity.OuverturePorte;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.OuverturePorteRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PorteService {

    private final OuverturePorteRepository repository;
    private final DiffusionService diffusion;
    private final PorteAlerteService alerteService;
    private final TaskScheduler planificateur;
    private final int delaiAlerteSecondes;

    public PorteService(OuverturePorteRepository repository,
                        DiffusionService diffusion,
                        PorteAlerteService alerteService,
                        @Qualifier("planificateur") TaskScheduler planificateur,
                        @Value("${frigo.delai-alerte-porte-secondes:30}") int delaiAlerteSecondes) {
        this.repository = repository;
        this.diffusion = diffusion;
        this.alerteService = alerteService;
        this.planificateur = planificateur;
        this.delaiAlerteSecondes = delaiAlerteSecondes;
    }

    @Transactional
    public PorteDto ouvrir(Utilisateur utilisateur) {
        if (repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc().isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTE_DEJA_OUVERTE", "La porte est déjà ouverte");
        }
        OuverturePorte session = new OuverturePorte();
        session.setUtilisateur(utilisateur);
        session = repository.save(session);
        diffusion.evenement("/topic/porte", "PORTE_OUVERTE");

        // Une seule vérification, programmée dans N secondes : pas de surveillance permanente
        Integer idOuverture = session.getId();
        planificateur.schedule(
                () -> alerteService.declencherSiNecessaire(idOuverture),
                Instant.now().plusSeconds(delaiAlerteSecondes));

        return versDto(session);
    }

    @Transactional
    public PorteDto fermer() {
        OuverturePorte session = repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc()
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT,
                        "AUCUNE_PORTE_OUVERTE", "La porte n'est pas ouverte"));
        session.fermer();
        session = repository.save(session);
        diffusion.evenement("/topic/porte", "PORTE_FERMEE");
        return versDto(session);
    }

    @Transactional(readOnly = true)
    public PorteDto etat() {
        Optional<OuverturePorte> session = repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc();
        if (session.isPresent()) {
            return versDto(session.get());
        }
        return new PorteDto(null, false, null, null, null, null, null, null);
    }

    private PorteDto versDto(OuverturePorte s) {
        return new PorteDto(
                s.getId(),
                s.estOuverte(),
                s.getDateOuverture(),
                s.getDateFermeture(),
                s.getUtilisateur().getId(),
                s.getUtilisateur().getNom(),
                s.calculerDureeOuverture().getSeconds(),
                s.getAlerteEnvoyee());
    }
}