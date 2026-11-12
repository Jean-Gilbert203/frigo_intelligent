package mg.bios.frigo.dto;

public record DictionnaireDto(
        Integer id,
        String nom,
        String image,
        Integer seuilMinimum,
        String categorie,
        String compartimentDefaut) {
}