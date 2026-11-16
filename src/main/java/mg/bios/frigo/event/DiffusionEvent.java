package mg.bios.frigo.event;

// Événement interne : "envoyer ce contenu sur cette destination"
public record DiffusionEvent(String destination, Object contenu) {
}