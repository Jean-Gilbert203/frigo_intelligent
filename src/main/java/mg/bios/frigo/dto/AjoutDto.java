package mg.bios.frigo.dto;

public record AjoutDto(LotAjouteDto lot, boolean fusionAvecLotExistant, String avertissement) {
}