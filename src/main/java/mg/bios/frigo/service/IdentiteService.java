package mg.bios.frigo.service;

import jakarta.servlet.http.HttpServletRequest;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// Niveau 1 : l'utilisateur est indiqué par l'en-tête X-Utilisateur-Id.
// Au niveau 2, seul le contenu de utilisateurAppelant() changera (lecture du jeton).
@Component
public class IdentiteService {

    public static final String ENTETE = "X-Utilisateur-Id";

    private final HttpServletRequest requete;
    private final UtilisateurRepository utilisateurRepository;

    public IdentiteService(HttpServletRequest requete, UtilisateurRepository utilisateurRepository) {
        this.requete = requete;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur utilisateurAppelant() {
        String valeur = requete.getHeader(ENTETE);
        if (valeur == null || valeur.isBlank()) {
            throw inconnu();
        }
        int id;
        try {
            id = Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            throw inconnu();
        }
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElseThrow(() -> inconnu());
        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "UTILISATEUR_INACTIF", "Ce compte est désactivé");
        }
        return utilisateur;
    }

    public Utilisateur exigerAdmin() {
        Utilisateur utilisateur = utilisateurAppelant();
        if (!utilisateur.estAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ACCES_ADMIN_REQUIS",
                    "Cette action est réservée à l'administrateur");
        }
        return utilisateur;
    }

    private ApiException inconnu() {
        return new ApiException(HttpStatus.NOT_FOUND, "UTILISATEUR_INCONNU",
                "En-tête " + ENTETE + " absent ou utilisateur inconnu");
    }
}