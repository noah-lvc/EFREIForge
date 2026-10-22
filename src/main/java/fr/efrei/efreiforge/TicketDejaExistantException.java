package fr.efrei.efreiforge;

public class TicketDejaExistantException extends RuntimeException {

  private final long identifiant;

  public TicketDejaExistantException(long identifiant) {
    super("Un ticket possède déjà l'identifiant " + identifiant);
    this.identifiant = identifiant;
  }

  public long identifiant() {
    return identifiant;
  }
}
