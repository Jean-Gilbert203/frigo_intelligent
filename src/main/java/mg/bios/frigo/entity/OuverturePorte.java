package mg.bios.frigo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Une session = une ouverture de la porte, de l'ouverture à la fermeture
@Entity
@Table(name = "ouvertures_porte")
public class OuverturePorte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ouverture")
    private Integer id;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    // Vide tant que la porte est ouverte
    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;

    @Column(name = "alerte_envoyee", nullable = false)
    private Boolean alerteEnvoyee = false;

    // Qui a ouvert la porte
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @PrePersist
    void avantInsertion() {
        if (dateOuverture == null) {
            dateOuverture = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

    public LocalDateTime getDateFermeture() { return dateFermeture; }
    public void setDateFermeture(LocalDateTime dateFermeture) { this.dateFermeture = dateFermeture; }

    public Boolean getAlerteEnvoyee() { return alerteEnvoyee; }
    public void setAlerteEnvoyee(Boolean alerteEnvoyee) { this.alerteEnvoyee = alerteEnvoyee; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    // Méthodes métier 
    public boolean estOuverte() {
        return dateFermeture == null;
    }

    public void fermer() {
        this.dateFermeture = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    // Durée calculée : jusqu'à la fermeture, ou jusqu'à maintenant si la porte est encore ouverte
    public Duration calculerDureeOuverture() {
        LocalDateTime fin = (dateFermeture != null) ? dateFermeture : LocalDateTime.now();
        return Duration.between(dateOuverture, fin);
    }

    public boolean estOuverteDepuisLongtemps(Duration delai) {
        return estOuverte() && calculerDureeOuverture().compareTo(delai) > 0;
    }
}