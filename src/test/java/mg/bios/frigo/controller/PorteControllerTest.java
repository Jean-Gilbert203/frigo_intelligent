package mg.bios.frigo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import mg.bios.frigo.dto.PorteDto;
import mg.bios.frigo.entity.Role;
import mg.bios.frigo.entity.Utilisateur;
import mg.bios.frigo.service.IdentiteService;
import mg.bios.frigo.service.PorteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PorteControllerTest {

    @Test
    void doitRetournerLEtatDeLaPorte() {
        PorteService porteService = mock(PorteService.class);
        IdentiteService identite = mock(IdentiteService.class);
        PorteController controller = new PorteController(porteService, identite);

        PorteDto etat = new PorteDto(
                1,
                false,
                null,
                null,
                2,
                "Enfant 1",
                0L,
                false
        );

        when(porteService.etat()).thenReturn(etat);

        PorteDto resultat = controller.etat();

        assertNotNull(resultat);
        assertEquals(false, resultat.ouverte());
        assertEquals(2, resultat.idUtilisateur());
        assertEquals("Enfant 1", resultat.nomUtilisateur());
    }

    @Test
    void doitOuvrirLaPorte() {
        PorteService porteService = mock(PorteService.class);
        IdentiteService identite = mock(IdentiteService.class);
        PorteController controller = new PorteController(porteService, identite);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(2);
        utilisateur.setNom("Enfant 1");
        utilisateur.setRole(Role.MEMBRE);
        utilisateur.setActif(true);

        PorteDto ouvert = new PorteDto(
                1,
                true,
                LocalDateTime.of(2026, 11, 15, 8, 39, 15),
                null,
                2,
                "Enfant 1",
                0L,
                false
        );

        when(identite.utilisateurAppelant()).thenReturn(utilisateur);
        when(porteService.ouvrir(utilisateur)).thenReturn(ouvert);

        ResponseEntity<PorteDto> reponse = controller.ouvrir();

        assertEquals(HttpStatus.CREATED, reponse.getStatusCode());
        assertNotNull(reponse.getBody());
        assertEquals(true, reponse.getBody().ouverte());
        assertEquals(2, reponse.getBody().idUtilisateur());
    }
}
