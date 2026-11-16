package mg.bios.frigo.dto;

import java.util.Map;

public record ErreurDto(String code, String message, Map<String, Object> details) {
}