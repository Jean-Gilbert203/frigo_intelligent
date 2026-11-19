package mg.bios.frigo.repository;

import java.util.List;
import mg.bios.frigo.entity.SupermarcheSimule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupermarcheSimuleRepository extends JpaRepository<SupermarcheSimule, Integer> {

    List<SupermarcheSimule> findAllByOrderByNomMagasinAsc();
}