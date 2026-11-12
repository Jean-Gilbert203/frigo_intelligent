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
@Table(name = "dictionnaire_aliments")
public class DictionnaireAliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dictionnaire")
    private Integer id;

    @Column(name = "nom_aliment_standard", nullable = false, unique = true, length = 100)
    private String nom;

    @Column(name = "image", nullable = false, length = 100)
    private String image;

    @Column(name = "seuil_minimum", nullable = false)
    private Integer seuilMinimum;

    // Plusieurs aliments pour une catégorie : c'est la clé étrangère id_categorie
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categorie", nullable = false)
    private Categorie categorie;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getSeuilMinimum() { return seuilMinimum; }
    public void setSeuilMinimum(Integer seuilMinimum) { this.seuilMinimum = seuilMinimum; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}