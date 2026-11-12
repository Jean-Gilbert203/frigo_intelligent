package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.UtilisateurDto;
import mg.bios.frigo.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurRepository repository;

    public UtilisateurController(UtilisateurRepository repository) {
        this.repository = repository;
    }

    // Les profils actifs, pour choisir son avatar sur l'écran du frigo
    @GetMapping
    public List<UtilisateurDto> lister() {
        return repository.findByActifTrueOrderByNom().stream()
                .map(u -> new UtilisateurDto(u.getId(), u.getNom(), u.getRole().name()))
                .toList();
    }
}