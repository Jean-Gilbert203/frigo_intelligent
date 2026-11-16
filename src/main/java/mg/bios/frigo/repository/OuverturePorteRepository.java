package mg.bios.frigo.repository;

import java.util.Optional;
import mg.bios.frigo.entity.OuverturePorte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OuverturePorteRepository extends JpaRepository<OuverturePorte, Integer> {

    // La session encore ouverte (date de fermeture vide), s'il y en a une
    Optional<OuverturePorte> findFirstByDateFermetureIsNullOrderByDateOuvertureDesc();
}