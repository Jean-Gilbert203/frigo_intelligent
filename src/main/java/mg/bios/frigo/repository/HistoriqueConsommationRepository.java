package mg.bios.frigo.repository;

import java.util.List;
import mg.bios.frigo.entity.HistoriqueConsommation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoriqueConsommationRepository extends JpaRepository<HistoriqueConsommation, Integer> {

    // Les prises les plus récentes d'abord ; Pageable limite le nombre de résultats
    @Query("SELECT h FROM HistoriqueConsommation h "
         + "JOIN FETCH h.utilisateur JOIN FETCH h.dictionnaire "
         + "ORDER BY h.dateConsommation DESC, h.id DESC")
    List<HistoriqueConsommation> findRecentes(Pageable pageable);

    @Query("SELECT h FROM HistoriqueConsommation h "
         + "JOIN FETCH h.utilisateur JOIN FETCH h.dictionnaire "
         + "WHERE h.utilisateur.id = :idUtilisateur "
         + "ORDER BY h.dateConsommation DESC, h.id DESC")
    List<HistoriqueConsommation> findRecentesParUtilisateur(
            @Param("idUtilisateur") Integer idUtilisateur, Pageable pageable);
}