package mg.bios.frigo.dto;

import java.time.LocalDateTime;

// Quand la porte est fermée et qu'aucune session n'existe, seul "ouverte" vaut false
public record PorteDto(
        Integer idOuverture,
        boolean ouverte,
        LocalDateTime dateOuverture,
        LocalDateTime dateFermeture,
        Integer idUtilisateur,
        String nomUtilisateur,
        Long dureeSecondes,
        Boolean alerteEnvoyee) {
}