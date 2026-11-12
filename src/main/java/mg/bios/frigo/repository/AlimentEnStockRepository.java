package mg.bios.frigo.repository;

import java.util.List;
import mg.bios.frigo.entity.AlimentEnStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlimentEnStockRepository extends JpaRepository<AlimentEnStock, Integer> {

    // Tous les lots, avec l'aliment, sa catégorie et le compartiment, dans un ordre lisible
    @Query("SELECT s FROM AlimentEnStock s "
         + "JOIN FETCH s.dictionnaire d "
         + "JOIN FETCH d.categorie "
         + "JOIN FETCH s.compartiment p "
         + "ORDER BY p.id, d.nom, s.datePeremption")
    List<AlimentEnStock> findAllAvecDetails();
}