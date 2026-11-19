package mg.bios.frigo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import mg.bios.frigo.dto.MagasinDto;
import mg.bios.frigo.dto.MagasinRequest;
import mg.bios.frigo.entity.SupermarcheSimule;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.SupermarcheSimuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MagasinService {

    private static final BigDecimal LATITUDE_MAX = BigDecimal.valueOf(90);
    private static final BigDecimal LONGITUDE_MAX = BigDecimal.valueOf(180);

    private final SupermarcheSimuleRepository repository;

    public MagasinService(SupermarcheSimuleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MagasinDto> lire() {
        return repository.findAllByOrderByNomMagasinAsc().stream().map(this::versDto).toList();
    }

    @Transactional
    public MagasinDto ajouter(MagasinRequest requete) {
        String nom = requete.nom() == null ? "" : requete.nom().trim();
        if (nom.isEmpty() || nom.length() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "NOM_INVALIDE",
                    "Le nom du magasin est obligatoire (100 caractères au maximum)");
        }
        BigDecimal latitude = requete.latitude();
        BigDecimal longitude = requete.longitude();
        if (latitude == null || longitude == null
                || latitude.abs().compareTo(LATITUDE_MAX) > 0
                || longitude.abs().compareTo(LONGITUDE_MAX) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "COORDONNEES_INVALIDES",
                    "Latitude entre -90 et 90, longitude entre -180 et 180");
        }
        SupermarcheSimule magasin = new SupermarcheSimule();
        magasin.setNomMagasin(nom);
        // Six décimales, comme la colonne de la base
        magasin.setLatitude(latitude.setScale(6, RoundingMode.HALF_UP));
        magasin.setLongitude(longitude.setScale(6, RoundingMode.HALF_UP));
        return versDto(repository.save(magasin));
    }

    // Les notifications qui citent ce magasin gardent leur texte ; leur lien devient NULL (ON DELETE SET NULL)
    @Transactional
    public void supprimer(Integer id) {
        SupermarcheSimule magasin = repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MAGASIN_INCONNU",
                        "Magasin inconnu : " + id));
        repository.delete(magasin);
    }

    private MagasinDto versDto(SupermarcheSimule m) {
        return new MagasinDto(m.getId(), m.getNomMagasin(), m.getLatitude(), m.getLongitude());
    }
}