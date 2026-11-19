package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.CourseDto;
import mg.bios.frigo.service.CourseService;
import mg.bios.frigo.service.IdentiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final IdentiteService identite;

    public CourseController(CourseService courseService, IdentiteService identite) {
        this.courseService = courseService;
        this.identite = identite;
    }

    @GetMapping
    public List<CourseDto> lire() {
        identite.exigerAdmin();
        return courseService.lire();
    }

    @PatchMapping("/{id}/achete")
    public CourseDto marquerAchete(@PathVariable Integer id) {
        identite.exigerAdmin();
        return courseService.marquerAchete(id);
    }

    // Action « Ne plus suivre » : supprime la ligne et les lots à 0 de l'aliment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> nePlusSuivre(@PathVariable Integer id) {
        identite.exigerAdmin();
        courseService.nePlusSuivre(id);
        return ResponseEntity.noContent().build();
    }
}