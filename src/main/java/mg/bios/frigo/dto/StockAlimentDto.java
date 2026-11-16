package mg.bios.frigo.dto;

import java.util.List;

public record StockAlimentDto(
        Integer idDictionnaire,
        String nom,
        String categorie,
        String image,
        Integer seuilMinimum,
        int quantite,
        int stockTotal,
        int stockDisponible,
        String statut,
        List<LotStockDto> lots) {
}