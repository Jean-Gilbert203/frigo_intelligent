package mg.bios.frigo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import mg.bios.frigo.dto.StockAlimentDto;
import mg.bios.frigo.dto.StockCompartimentDto;
import mg.bios.frigo.service.StockService;
import org.junit.jupiter.api.Test;

class StockControllerTest {

    @Test
    void doitRetournerLesStocks() {
        StockService stockService = mock(StockService.class);
        StockController controller = new StockController(stockService);

        StockAlimentDto aliment = new StockAlimentDto(
                1,
                "Tomates",
                "Légumes",
                "/images/aliments/tomates.png",
                2,
                2,
                5,
                5,
                "OK",
                List.of()
        );

        StockCompartimentDto dto = new StockCompartimentDto(
                1,
                "Compartiment 1",
                new BigDecimal("4.5"),
                20,
                50,
                List.of(aliment)
        );

        when(stockService.lireStocks()).thenReturn(List.of(dto));

        List<StockCompartimentDto> resultats = controller.stocks();

        assertNotNull(resultats);
        assertEquals(1, resultats.size());
        assertEquals("Compartiment 1", resultats.get(0).nom());
        assertEquals(1, resultats.get(0).aliments().size());
        assertEquals("Tomates", resultats.get(0).aliments().get(0).nom());
        assertEquals("OK", resultats.get(0).aliments().get(0).statut());
    }
}
