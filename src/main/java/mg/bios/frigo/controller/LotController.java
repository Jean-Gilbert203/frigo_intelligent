package mg.bios.frigo.controller;

import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.LotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frigo/lots")
public class LotController {

    private final LotService lotService;
    private final IdentiteService identite;

    public LotController(LotService lotService, IdentiteService identite) {
        this.lotService = lotService;
        this.identite = identite;
    }

    // Jeter un lot périmé (ou vide) : réservé à l'administrateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> jeter(@PathVariable Integer id) {
        identite.exigerAdmin();
        lotService.jeter(id);
        return ResponseEntity.noContent().build();
    }
}