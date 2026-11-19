package mg.bios.frigo.controller;

import mg.bios.frigo.dto.AvanceeTempsDto;
import mg.bios.frigo.dto.AvanceeTempsRequest;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.SimulationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;
    private final IdentiteService identite;

    public SimulationController(SimulationService simulationService, IdentiteService identite) {
        this.simulationService = simulationService;
        this.identite = identite;
    }

    @PostMapping("/avancer-temps")
    public AvanceeTempsDto avancerTemps(@RequestBody AvanceeTempsRequest requete) {
        identite.utilisateurAppelant();
        return simulationService.avancerTemps(requete.jours());
    }
}