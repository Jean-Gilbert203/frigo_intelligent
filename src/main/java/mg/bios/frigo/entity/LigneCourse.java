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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "liste_courses")
public class LigneCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_course")
    private Integer id;

    @Column(name = "quantite_recommandee", nullable = false)
    private Integer quantiteRecommandee = 1;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutCourse statut = StatutCourse.A_ACHETER;

    // Au plus une ligne par aliment : la colonne est unique en base
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dictionnaire", nullable = false, unique = true)
    private DictionnaireAliment dictionnaire;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantiteRecommandee() { return quantiteRecommandee; }
    public void setQuantiteRecommandee(Integer quantiteRecommandee) { this.quantiteRecommandee = quantiteRecommandee; }

    public StatutCourse getStatut() { return statut; }
    public void setStatut(StatutCourse statut) { this.statut = statut; }

    public DictionnaireAliment getDictionnaire() { return dictionnaire; }
    public void setDictionnaire(DictionnaireAliment dictionnaire) { this.dictionnaire = dictionnaire; }

    public void marquerAchete() {
        this.statut = StatutCourse.ACHETE;
    }

    public void remettreAAcheter() {
        this.statut = StatutCourse.A_ACHETER;
    }
}