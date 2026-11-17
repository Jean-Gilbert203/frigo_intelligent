package mg.bios.frigo.dto;

import java.util.List;

public record ConsommationDto(
        Integer idAliment,
        String nom,
        int quantitePrise,
        int stockDisponible,
        int seuilMinimum,
        String statut,
        boolean ajouteAuxCourses,
        List<LotModifieDto> lotsTouches) {
}