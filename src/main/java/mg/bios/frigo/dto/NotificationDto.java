package mg.bios.frigo.dto;

public record NotificationDto(
        Integer id,
        String titre,
        String message,
        String type,
        String dateEnvoi,
        boolean lu,
        Integer idAliment,
        String nomAliment,
        Integer idOuverture,
        Integer idMagasin) {
}