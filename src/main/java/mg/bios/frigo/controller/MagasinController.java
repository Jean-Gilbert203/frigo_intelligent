package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.MagasinDto;
import mg.bios.frigo.dto.MagasinRequest;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.MagasinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/magasins")
public class MagasinController {

    private final MagasinService magasinService;
    private final IdentiteService identite;

    public MagasinController(MagasinService magasinService, IdentiteService identite) {
        this.magasinService = magasinService;
        this.identite = identite;
    }

    @GetMapping
    public List<MagasinDto> lire() {
        identite.exigerAdmin();
        return magasinService.lire();
    }

    @PostMapping
    public ResponseEntity<MagasinDto> ajouter(@RequestBody MagasinRequest requete) {
        identite.exigerAdmin();
        return ResponseEntity.status(HttpStatus.CREATED).body(magasinService.ajouter(requete));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
        identite.exigerAdmin();
        magasinService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}