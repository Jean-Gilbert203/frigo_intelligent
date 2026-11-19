package mg.bios.frigo.dto;

public record HistoriqueDto(
        Integer id,
        String utilisateur,
        String aliment,
        String image,
        int quantitePrise,
        String dateConsommation) {
}