package mg.bios.frigo.service;

import java.util.Optional;
import mg.bios.frigo.entity.DictionnaireAliment;
import mg.bios.frigo.entity.LigneCourse;
import mg.bios.frigo.entity.StatutCourse;
import mg.bios.frigo.repository.LigneCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final LigneCourseRepository repository;

    public CourseService(LigneCourseRepository repository) {
        this.repository = repository;
    }

    // Aligne la liste de courses sur le stock disponible (règle R5).
    // Retourne vrai si la liste a changé.
    @Transactional
    public boolean synchroniser(DictionnaireAliment aliment, int disponible) {
        Optional<LigneCourse> existante = repository.findByDictionnaireId(aliment.getId());

        // Stock bas ou nul : il faut acheter
        if (disponible <= aliment.getSeuilMinimum()) {
            if (existante.isEmpty()) {
                LigneCourse ligne = new LigneCourse();
                ligne.setDictionnaire(aliment);
                repository.save(ligne);
                return true;
            }
            LigneCourse ligne = existante.get();
            if (ligne.getStatut() == StatutCourse.A_ACHETER) {
                return false;
            }
            ligne.remettreAAcheter();
            repository.save(ligne);
            return true;
        }

        // Stock suffisant : une ligne « à acheter » passe à « acheté »
        if (existante.isPresent() && existante.get().getStatut() == StatutCourse.A_ACHETER) {
            LigneCourse ligne = existante.get();
            ligne.marquerAchete();
            repository.save(ligne);
            return true;
        }
        return false;
    }
}