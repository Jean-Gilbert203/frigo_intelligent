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
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_consommation")
public class HistoriqueConsommation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Integer id;

    @Column(name = "quantite_prise", nullable = false)
    private Integer quantitePrise = 1;

    @Column(name = "date_consommation", nullable = false)
    private LocalDateTime dateConsommation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dictionnaire", nullable = false)
    private DictionnaireAliment dictionnaire;

    // Si la date est vide, elle prend le moment de l'enregistrement
    @PrePersist
    void avantInsertion() {
        if (dateConsommation == null) {
            dateConsommation = LocalDateTime.now();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantitePrise() { return quantitePrise; }
    public void setQuantitePrise(Integer quantitePrise) { this.quantitePrise = quantitePrise; }

    public LocalDateTime getDateConsommation() { return dateConsommation; }
    public void setDateConsommation(LocalDateTime dateConsommation) { this.dateConsommation = dateConsommation; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public DictionnaireAliment getDictionnaire() { return dictionnaire; }
    public void setDictionnaire(DictionnaireAliment dictionnaire) { this.dictionnaire = dictionnaire; }
}