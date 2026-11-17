package mg.bios.frigo.dto;

import java.time.LocalDate;

// On indique SOIT idDictionnaire, SOIT nomAliment. Sans idCompartiment,
// le compartiment par défaut de la catégorie est utilisé.
public record AjoutRequest(
        Integer idDictionnaire,
        String nomAliment,
        Integer quantite,
        LocalDate datePeremption,
        Integer idCompartiment) {
}