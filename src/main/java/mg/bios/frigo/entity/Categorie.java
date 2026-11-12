package mg.bios.frigo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    private Integer id;

    @Column(name = "nom_categorie", nullable = false, unique = true, length = 50)
    private String nom;

    // Plusieurs catégories pour un compartiment : c'est la clé étrangère
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_compartiment_defaut", nullable = false)
    private Compartiment compartimentDefaut;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Compartiment getCompartimentDefaut() { return compartimentDefaut; }
    public void setCompartimentDefaut(Compartiment compartimentDefaut) { this.compartimentDefaut = compartimentDefaut; }
}