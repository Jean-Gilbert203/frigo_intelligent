package mg.bios.frigo.controller;

import java.util.List;
import mg.bios.frigo.dto.StockCompartimentDto;
import mg.bios.frigo.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frigo")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stocks")
    public List<StockCompartimentDto> stocks() {
        return stockService.lireStocks();
    }
}