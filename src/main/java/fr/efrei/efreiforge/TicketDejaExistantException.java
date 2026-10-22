package fr.efrei.efreiforge;

public class TicketDejaExistantException extends RuntimeException {

  private final String identifiant;

  public TicketDejaExistantException(String identifiant) {
    super("Un ticket possède déjà l'identifiant " + identifiant);
    this.identifiant = identifiant;
  }

  public String identifiant() {
    return identifiant;
  }
}
