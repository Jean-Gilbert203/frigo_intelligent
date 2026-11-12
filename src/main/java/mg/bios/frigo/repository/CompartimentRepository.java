package mg.bios.frigo.repository;

import mg.bios.frigo.entity.Compartiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompartimentRepository extends JpaRepository<Compartiment, Integer> {
}