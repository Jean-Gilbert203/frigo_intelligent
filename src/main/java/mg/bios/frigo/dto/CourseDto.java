package mg.bios.frigo.dto;

public record CourseDto(
        Integer id,
        Integer idAliment,
        String nom,
        String image,
        int quantiteRecommandee,
        String statut) {
}