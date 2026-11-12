package mg.bios.frigo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "compartiments")
public class Compartiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compartiment")
    private Integer id;

    @Column(name = "nom_compartiment", nullable = false, length = 50)
    private String nom;

    @Column(name = "temperature_actuelle", nullable = false, precision = 4, scale = 2)
    private BigDecimal temperatureActuelle;

    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public BigDecimal getTemperatureActuelle() { return temperatureActuelle; }
    public void setTemperatureActuelle(BigDecimal temperatureActuelle) { this.temperatureActuelle = temperatureActuelle; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }
}