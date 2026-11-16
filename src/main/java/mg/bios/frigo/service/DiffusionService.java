package mg.bios.frigo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import mg.bios.frigo.dto.EvenementDto;
import mg.bios.frigo.event.DiffusionEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class DiffusionService {

    private final ApplicationEventPublisher publisher;

    public DiffusionService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void diffuser(String destination, Object contenu) {
        publisher.publishEvent(new DiffusionEvent(destination, contenu));
    }

    // Raccourci : un petit événement « signal » (type + date)
    public void evenement(String destination, String type) {
        String date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        diffuser(destination, new EvenementDto(type, date));
    }
}