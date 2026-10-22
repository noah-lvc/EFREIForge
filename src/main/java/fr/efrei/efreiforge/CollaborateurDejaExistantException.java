package fr.efrei.efreiforge;

public class CollaborateurDejaExistantException extends RuntimeException {

  private final String identifiant;

  public CollaborateurDejaExistantException(String identifiant) {
    super("Un collaborateur possède déjà l'identifiant " + identifiant);
    this.identifiant = identifiant;
  }

  public String identifiant() {
    return identifiant;
  }
}
