package mg.bios.frigo.service;

import java.time.LocalDate;
import mg.bios.frigo.entity.AlimentEnStock;
import mg.bios.frigo.exception.ApiException;
import mg.bios.frigo.repository.AlimentEnStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LotService {

    private final AlimentEnStockRepository stockRepository;
    private final DiffusionService diffusion;

    public LotService(AlimentEnStockRepository stockRepository, DiffusionService diffusion) {
        this.stockRepository = stockRepository;
        this.diffusion = diffusion;
    }

    @Transactional
    public void jeter(Integer idLot) {
        AlimentEnStock lot = stockRepository.findById(idLot)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "LOT_INCONNU",
                        "Lot inconnu : " + idLot));
        if (!lot.estPerime(LocalDate.now()) && !lot.estVide()) {
            throw new ApiException(HttpStatus.CONFLICT, "LOT_NON_JETABLE",
                    "Ce lot n'est ni périmé ni vide : il ne peut pas être jeté");
        }
        stockRepository.delete(lot);
        diffusion.evenement("/topic/stocks", "STOCK_MODIFIE");
    }
}