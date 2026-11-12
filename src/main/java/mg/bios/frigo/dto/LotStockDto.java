package mg.bios.frigo.dto;

import java.time.LocalDate;

public record LotStockDto(
        Integer id,
        Integer quantite,
        LocalDate dateAjout,
        LocalDate datePeremption,
        long joursRestants,
        String etat) {
}