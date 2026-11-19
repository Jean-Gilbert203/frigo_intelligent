package mg.bios.frigo.repository;

import java.util.List;
import mg.bios.frigo.entity.Role;
import mg.bios.frigo.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    // Spring Data comprend le nom : utilisateurs actifs, triés par nom
    List<Utilisateur> findByActifTrueOrderByNom();

    // Les administrateurs actifs (destinataires des alertes)
    List<Utilisateur> findByRoleAndActifTrue(Role role);
}