package mg.bios.frigo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "supermarches_simules")
public class SupermarcheSimule {

    private static final double RAYON_TERRE_METRES = 6371000;
    private static final double SEUIL_PROCHE_METRES = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_magasin")
    private Integer id;

    @Column(name = "nom_magasin", nullable = false, length = 100)
    private String nomMagasin;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomMagasin() { return nomMagasin; }
    public void setNomMagasin(String nomMagasin) { this.nomMagasin = nomMagasin; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    // Distance en mètres entre ce magasin et une position (formule de Haversine)
    public double distanceVers(double lat, double lon) {
        double lat1 = Math.toRadians(latitude.doubleValue());
        double lat2 = Math.toRadians(lat);
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(lon - longitude.doubleValue());
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        return 2 * RAYON_TERRE_METRES * Math.asin(Math.sqrt(a));
    }

    // Vrai si la position est à moins de 500 m du magasin
    public boolean estProche(double lat, double lon) {
        return distanceVers(lat, lon) < SEUIL_PROCHE_METRES;
    }
}