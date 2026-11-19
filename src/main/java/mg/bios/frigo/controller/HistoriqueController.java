package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.HistoriqueDto;
import mg.bios.frigo.service.HistoriqueService;
import mg.bios.frigo.service.IdentiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historique")
public class HistoriqueController {

    private final HistoriqueService historiqueService;
    private final IdentiteService identite;

    public HistoriqueController(HistoriqueService historiqueService, IdentiteService identite) {
        this.historiqueService = historiqueService;
        this.identite = identite;
    }

    // Réservé à l'administrateur ; limite (20 par défaut) et filtre facultatif par utilisateur
    @GetMapping
    public List<HistoriqueDto> lire(@RequestParam(defaultValue = "20") int limite,
                                    @RequestParam(required = false) Integer idUtilisateur) {
        identite.exigerAdmin();
        return historiqueService.lire(limite, idUtilisateur);
    }
}