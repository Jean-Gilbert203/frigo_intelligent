package mg.bios.frigo.dto;

import java.math.BigDecimal;
import java.util.List;

public record StockCompartimentDto(
        Integer id,
        String nom,
        BigDecimal temperature,
        Integer capaciteMax,
        int remplissage,
        List<StockAlimentDto> aliments) {
}