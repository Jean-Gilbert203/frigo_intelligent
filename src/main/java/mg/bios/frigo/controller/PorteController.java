package mg.bios.frigo.controller;

import mg.bios.frigo.dto.PorteDto;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.PorteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frigo/porte")
public class PorteController {

    private final PorteService porteService;
    private final IdentiteService identite;

    public PorteController(PorteService porteService, IdentiteService identite) {
        this.porteService = porteService;
        this.identite = identite;
    }

    @PostMapping("/ouvrir")
    public ResponseEntity<PorteDto> ouvrir() {
        Utilisateur appelant = identite.utilisateurAppelant();
        return ResponseEntity.status(HttpStatus.CREATED).body(porteService.ouvrir(appelant));
    }

    @PostMapping("/fermer")
    public PorteDto fermer() {
        identite.utilisateurAppelant();
        return porteService.fermer();
    }

    @GetMapping
    public PorteDto etat() {
        return porteService.etat();
    }
}