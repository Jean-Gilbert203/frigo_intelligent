package mg.bios.frigo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import mg.bios.frigo.dto.PorteDto;
import mg.bios.frigo.entity.OuverturePorte;
import mg.bios.frigo.entity.Role;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.OuverturePorteRepository;
import org.junit.jupiter.api.Test;

class PorteServiceTest {

    @Test
    void ouvrir_shouldThrowConflict_whenDoorAlreadyOpen() {
        OuverturePorteRepository repository = mock(OuverturePorteRepository.class);
        DiffusionService diffusion = mock(DiffusionService.class);
        PorteService service = new PorteService(repository, diffusion);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(2);
        utilisateur.setNom("Enfant 1");
        utilisateur.setRole(Role.MEMBRE);
        utilisateur.setActif(true);

        OuverturePorte sessionOuverte = new OuverturePorte();
        sessionOuverte.setId(1);
        sessionOuverte.setDateOuverture(LocalDateTime.now().minusMinutes(5));
        sessionOuverte.setUtilisateur(utilisateur);

        when(repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc())
                .thenReturn(Optional.of(sessionOuverte));

        ApiException ex = assertThrows(ApiException.class, () -> service.ouvrir(utilisateur));

        assertEquals("PORTE_DEJA_OUVERTE", ex.getCode());
        verify(diffusion, never()).evenement("/topic/porte", "PORTE_OUVERTE");
    }

    @Test
    void etat_shouldReturnClosedDoor_whenNoOpenSession() {
        OuverturePorteRepository repository = mock(OuverturePorteRepository.class);
        DiffusionService diffusion = mock(DiffusionService.class);
        PorteService service = new PorteService(repository, diffusion);

        when(repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc())
                .thenReturn(Optional.empty());

        PorteDto dto = service.etat();

        assertNotNull(dto);
        assertEquals(false, dto.ouverte());
        assertEquals(null, dto.idUtilisateur());
    }

    @Test
    void fermer_shouldCloseDoorAndDiffuseEvent() {
        OuverturePorteRepository repository = mock(OuverturePorteRepository.class);
        DiffusionService diffusion = mock(DiffusionService.class);
        PorteService service = new PorteService(repository, diffusion);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(2);
        utilisateur.setNom("Enfant 1");
        utilisateur.setRole(Role.MEMBRE);
        utilisateur.setActif(true);

        OuverturePorte session = new OuverturePorte();
        session.setId(1);
        session.setDateOuverture(LocalDateTime.now().minusMinutes(2));
        session.setUtilisateur(utilisateur);

        when(repository.findFirstByDateFermetureIsNullOrderByDateOuvertureDesc())
                .thenReturn(Optional.of(session));
        when(repository.save(session)).thenReturn(session);

        PorteDto dto = service.fermer();

        assertNotNull(dto);
        assertEquals(false, dto.ouverte());
        assertEquals(2, dto.idUtilisateur());
        verify(diffusion).evenement("/topic/porte", "PORTE_FERMEE");
    }
}
