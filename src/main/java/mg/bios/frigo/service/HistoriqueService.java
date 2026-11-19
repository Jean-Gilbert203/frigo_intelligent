package mg.bios.frigo.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import mg.bios.frigo.dto.HistoriqueDto;
import mg.bios.frigo.entity.HistoriqueConsommation;
import mg.bios.frigo.repository.HistoriqueConsommationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoriqueService {

    private final HistoriqueConsommationRepository repository;

    public HistoriqueService(HistoriqueConsommationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HistoriqueDto> lire(int limite, Integer idUtilisateur) {
        PageRequest page = PageRequest.of(0, Math.max(1, Math.min(limite, 200)));
        List<HistoriqueConsommation> prises = (idUtilisateur == null)
                ? repository.findRecentes(page)
                : repository.findRecentesParUtilisateur(idUtilisateur, page);
        return prises.stream().map(this::versDto).toList();
    }

    private HistoriqueDto versDto(HistoriqueConsommation h) {
        return new HistoriqueDto(
                h.getId(),
                h.getUtilisateur().getNom(),
                h.getDictionnaire().getNom(),
                "/images/aliments/" + h.getDictionnaire().getImage(),
                h.getQuantitePrise(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(h.getDateConsommation()));
    }
}