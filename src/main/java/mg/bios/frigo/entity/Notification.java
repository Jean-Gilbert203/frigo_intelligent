package mg.bios.frigo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer id;

    @Column(name = "titre", nullable = false, length = 150)
    private String titre;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "type", nullable = false, length = 30)
    private TypeNotification type;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    // Qui reçoit la notification
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur destinataire;

    // Facultatif : l'aliment concerné (péremption, stock faible, rupture)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dictionnaire")
    private DictionnaireAliment aliment;

    // Facultatif : la session de porte concernée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ouverture")
    private OuverturePorte ouverture;

    @PrePersist
    void avantInsertion() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public TypeNotification getType() { return type; }
    public void setType(TypeNotification type) { this.type = type; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Boolean getLu() { return lu; }
    public void setLu(Boolean lu) { this.lu = lu; }

    public Utilisateur getDestinataire() { return destinataire; }
    public void setDestinataire(Utilisateur destinataire) { this.destinataire = destinataire; }

    public DictionnaireAliment getAliment() { return aliment; }
    public void setAliment(DictionnaireAliment aliment) { this.aliment = aliment; }

    public OuverturePorte getOuverture() { return ouverture; }
    public void setOuverture(OuverturePorte ouverture) { this.ouverture = ouverture; }

    public SupermarcheSimule getMagasin() { return magasin; }
    public void setMagasin(SupermarcheSimule magasin) { this.magasin = magasin; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin")
    private SupermarcheSimule magasin;

    // Méthode métier du diagramme de classes
    public void marquerCommeLue() {
        this.lu = true;
    }
}