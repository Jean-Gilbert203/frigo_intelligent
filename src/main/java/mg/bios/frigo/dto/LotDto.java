package mg.bios.frigo.dto;

import java.time.LocalDate;

public record LotDto(
        Integer id,
        String aliment,
        String categorie,
        String image,
        Integer quantite,
        LocalDate dateAjout,
        LocalDate datePeremption,
        String compartiment) {
}