package mg.bios.frigo.exception;

import mg.bios.frigo.dto.ErreurDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurDto> surApiException(ApiException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErreurDto(e.getCode(), e.getMessage(), e.getDetails()));
    }
}