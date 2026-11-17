package mg.bios.frigo.repository;

import java.util.Optional;
import mg.bios.frigo.entity.LigneCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCourseRepository extends JpaRepository<LigneCourse, Integer> {

    // La ligne de courses d'un aliment, s'il y en a une (au plus une par aliment)
    Optional<LigneCourse> findByDictionnaireId(Integer idDictionnaire);
}