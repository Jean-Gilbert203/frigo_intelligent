package mg.bios.frigo.repository;

import java.util.List;
import java.util.Optional;
import mg.bios.frigo.entity.LigneCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LigneCourseRepository extends JpaRepository<LigneCourse, Integer> {

    // La ligne de courses d'un aliment, s'il y en a une (au plus une par aliment)
    Optional<LigneCourse> findByDictionnaireId(Integer idDictionnaire);

    // Toutes les lignes, avec leur aliment
    @Query("SELECT l FROM LigneCourse l JOIN FETCH l.dictionnaire d ORDER BY d.nom")
    List<LigneCourse> findAllAvecAliment();
}