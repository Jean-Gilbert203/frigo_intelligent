package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.MarquageDto;
import mg.bios.frigo.dto.NotificationDto;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final IdentiteService identite;

    public NotificationController(NotificationService notificationService, IdentiteService identite) {
        this.notificationService = notificationService;
        this.identite = identite;
    }

    // Le destinataire est l'utilisateur de l'en-tête X-Utilisateur-Id
    @GetMapping
    public List<NotificationDto> lire(@RequestParam(defaultValue = "50") int limite) {
        Utilisateur appelant = identite.utilisateurAppelant();
        return notificationService.lire(appelant.getId(), limite);
    }

    @GetMapping("/non-lues")
    public List<NotificationDto> nonLues() {
        Utilisateur appelant = identite.utilisateurAppelant();
        return notificationService.nonLues(appelant.getId());
    }

    @PatchMapping("/{id}/lue")
    public NotificationDto marquerLue(@PathVariable Integer id) {
        Utilisateur appelant = identite.utilisateurAppelant();
        return notificationService.marquerLue(id, appelant);
    }

    @PatchMapping("/lire-tout")
    public MarquageDto marquerToutesLues() {
        Utilisateur appelant = identite.utilisateurAppelant();
        return notificationService.marquerToutesLues(appelant);
    }
}