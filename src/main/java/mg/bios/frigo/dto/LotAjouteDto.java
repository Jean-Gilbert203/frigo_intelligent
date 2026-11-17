package mg.bios.frigo.dto;

import java.time.LocalDate;

public record LotAjouteDto(
        Integer id,
        String aliment,
        int quantite,
        LocalDate dateAjout,
        LocalDate datePeremption,
        String compartiment) {
}