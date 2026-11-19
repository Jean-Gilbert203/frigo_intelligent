package mg.bios.frigo.dto;

import java.math.BigDecimal;

public record MagasinDto(Integer id, String nom, BigDecimal latitude, BigDecimal longitude) {
}