package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.AjoutDto;
import mg.bios.frigo.dto.AjoutRequest;
import mg.bios.frigo.dto.ConsommationDto;
import mg.bios.frigo.dto.ConsommationRequest;
import mg.bios.frigo.dto.StockCompartimentDto;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.service.AjoutService;
import mg.bios.frigo.service.ConsommationService;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frigo")
public class StockController {

    private final StockService stockService;
    private final ConsommationService consommationService;
    private final AjoutService ajoutService;
    private final IdentiteService identite;

    public StockController(StockService stockService,
                           ConsommationService consommationService,
                           AjoutService ajoutService,
                           IdentiteService identite) {
        this.stockService = stockService;
        this.consommationService = consommationService;
        this.ajoutService = ajoutService;
        this.identite = identite;
    }

    @GetMapping("/stocks")
    public List<StockCompartimentDto> stocks() {
        return stockService.lireStocks();
    }

    // idAliment = identifiant du DICTIONNAIRE (pas d'un lot) ; l'utilisateur vient de l'en-tête
    @PutMapping("/consommer/{idAliment}")
    public ConsommationDto consommer(@PathVariable Integer idAliment,
                                     @RequestBody ConsommationRequest requete) {
        Utilisateur appelant = identite.utilisateurAppelant();
        return consommationService.consommer(idAliment, requete.quantite(), appelant);
    }

    // Réservé à l'administrateur
    @PostMapping("/ajouter")
    public ResponseEntity<AjoutDto> ajouter(@RequestBody AjoutRequest requete) {
        identite.exigerAdmin();
        return ResponseEntity.status(HttpStatus.CREATED).body(ajoutService.ajouter(requete));
    }
}