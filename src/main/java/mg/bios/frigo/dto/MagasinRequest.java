package mg.bios.frigo.dto;

import java.math.BigDecimal;

public record MagasinRequest(String nom, BigDecimal latitude, BigDecimal longitude) {
}