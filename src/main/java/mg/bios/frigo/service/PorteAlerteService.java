package mg.bios.frigo.service;

import java.util.LinkedHashMap;
import java.util.Map;
import mg.bios.frigo.entity.OuverturePorte;
import mg.bios.frigo.entity.Role;
import mg.bios.frigo.entity.TypeNotification;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.repository.OuverturePorteRepository;
import mg.bios.frigo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PorteAlerteService {

    private final OuverturePorteRepository repository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final DiffusionService diffusion;
    private final int delaiSecondes;

    public PorteAlerteService(OuverturePorteRepository repository,
                              UtilisateurRepository utilisateurRepository,
                              NotificationService notificationService,
                              DiffusionService diffusion,
                              @Value("${frigo.delai-alerte-porte-secondes:30}") int delaiSecondes) {
        this.repository = repository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
        this.diffusion = diffusion;
        this.delaiSecondes = delaiSecondes;
    }

    @Transactional
    public void declencherSiNecessaire(Integer idOuverture) {
        OuverturePorte session = repository.findById(idOuverture).orElse(null);
        // Porte déjà fermée, ou alerte déjà envoyée : rien à faire (une alerte par session)
        if (session == null || !session.estOuverte() || Boolean.TRUE.equals(session.getAlerteEnvoyee())) {
            return;
        }
        session.setAlerteEnvoyee(true);
        repository.save(session);

        Utilisateur ouvreur = session.getUtilisateur();
        String message = "La porte est ouverte depuis plus de " + delaiSecondes
                + " secondes (ouverte par " + ouvreur.getNom() + ").";

        // Destinataires : la personne qui a ouvert, puis les administrateurs, sans doublon
        Map<Integer, Utilisateur> destinataires = new LinkedHashMap<>();
        destinataires.put(ouvreur.getId(), ouvreur);
        for (Utilisateur admin : utilisateurRepository.findByRoleAndActifTrue(Role.ADMIN)) {
            destinataires.putIfAbsent(admin.getId(), admin);
        }
        for (Utilisateur destinataire : destinataires.values()) {
            notificationService.notifier(destinataire, TypeNotification.PORTE_OUVERTE,
                    "Porte ouverte trop longtemps", message, null, session, null);
        }

        diffusion.evenement("/topic/porte", "PORTE_ALERTE");
    }
}