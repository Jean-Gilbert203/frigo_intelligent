package mg.bios.frigo.repository;

import java.util.List;
import java.util.Optional;
import mg.bios.frigo.entity.DictionnaireAliment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DictionnaireAlimentRepository extends JpaRepository<DictionnaireAliment, Integer> {

    // JOIN FETCH : charge d'un coup la catégorie et son compartiment
    @Query("SELECT d FROM DictionnaireAliment d "
         + "JOIN FETCH d.categorie c "
         + "JOIN FETCH c.compartimentDefaut "
         + "ORDER BY d.nom")
    List<DictionnaireAliment> findAllAvecCategorie();

    // Recherche par nom : insensible à la casse et aux accents 
    Optional<DictionnaireAliment> findByNom(String nom);
}