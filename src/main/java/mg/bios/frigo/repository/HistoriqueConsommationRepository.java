package mg.bios.frigo.repository;

import mg.bios.frigo.entity.HistoriqueConsommation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriqueConsommationRepository extends JpaRepository<HistoriqueConsommation, Integer> {
}