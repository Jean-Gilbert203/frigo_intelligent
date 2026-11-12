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
import java.time.LocalDate;

// Une ligne de cette table = un LOT (mêmes aliment, date de péremption et compartiment)
@Entity
@Table(name = "aliments_en_stock")
public class AlimentEnStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aliment")
    private Integer id;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "date_ajout", nullable = false)
    private LocalDate dateAjout;

    @Column(name = "date_peremption", nullable = false)
    private LocalDate datePeremption;

    // Père 1 : l'aliment du dictionnaire
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dictionnaire", nullable = false)
    private DictionnaireAliment dictionnaire;

    // Père 2 : le compartiment où le lot est rangé
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compartiment", nullable = false)
    private Compartiment compartiment;

    // Si on oublie la date d'ajout, elle prend automatiquement la date du jour
    @PrePersist
    void avantInsertion() {
        if (dateAjout == null) {
            dateAjout = LocalDate.now();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }

    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }

    public DictionnaireAliment getDictionnaire() { return dictionnaire; }
    public void setDictionnaire(DictionnaireAliment dictionnaire) { this.dictionnaire = dictionnaire; }

    public Compartiment getCompartiment() { return compartiment; }
    public void setCompartiment(Compartiment compartiment) { this.compartiment = compartiment; }
}