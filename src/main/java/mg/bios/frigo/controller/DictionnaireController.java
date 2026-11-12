package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.DictionnaireDto;
import mg.bios.frigo.repository.DictionnaireAlimentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dictionnaire")
public class DictionnaireController {

    private final DictionnaireAlimentRepository repository;

    public DictionnaireController(DictionnaireAlimentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DictionnaireDto> lister() {
        return repository.findAllAvecCategorie().stream()
                .map(d -> new DictionnaireDto(
                        d.getId(),
                        d.getNom(),
                        "/images/aliments/" + d.getImage(),
                        d.getSeuilMinimum(),
                        d.getCategorie().getNom(),
                        d.getCategorie().getCompartimentDefaut().getNom()))
                .toList();
    }
}