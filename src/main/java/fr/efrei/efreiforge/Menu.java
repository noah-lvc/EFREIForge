package fr.efrei.efreiforge;

import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final CollaborateurService collaborateurService;
    private final TicketService ticketService;

    public Menu(EntityManagerFactory fabrique) {
        this.scanner = new Scanner(System.in);
        this.collaborateurService =
                new CollaborateurService(fabrique);
        this.ticketService =
                new TicketService(fabrique);
    }

    public void lancer() {
        boolean continuer = true;

        while (continuer) {

            System.out.println("\n=== EFREIForge ===");
            System.out.println("1 - Gestion Collaborateurs");
            System.out.println("2 - Gestion Tickets");
            System.out.println("0 - Quitter");

            switch (scanner.nextLine()) {
                case "1" -> menuCollaborateurs();
                case "2" -> menuTickets();
                case "0" -> continuer = false;


            }
        }
    }

    //region menuCollaborateurs

    private void menuCollaborateurs() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Gestion Collaborateurs ===");
            System.out.println("1 - Créer un collaborateur");
            System.out.println("2 - Gestion des collaborateurs");
            System.out.println("3 - Voir les collaborateurs");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> creerCollaborateur();
                case "2" -> gestionCollaborateurs();
                case "3" -> voirCollaborateurs();
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    private void creerCollaborateur() {
        Collaborateur collaborateur = null;
        boolean choixValide = false;

        while (!choixValide) {
            // Le menu doit être DANS la boucle pour s'afficher à nouveau en cas d'erreur
            System.out.println("Quel type de collaborateur voulez-vous ajouter ?");
            System.out.println("1 - Programmeur");
            System.out.println("2 - Testeur");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> {
                    collaborateur = new Programmeur(scanner);
                    choixValide = true;
                }
                case "2" -> {
                    collaborateur = new Testeur(scanner);
                    choixValide = true;
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Choix invalide. Veuillez réessayer.\n");
            }
        }

        try {
            collaborateurService.ajouter(collaborateur);
            System.out.println(collaborateur.getPrenom() + " (" + collaborateur.getIdentifiant() + ") a été ajouté(e).");
        } catch (CollaborateurDejaExistantException e) {
            System.out.println("Impossible d'ajouter " + collaborateur.getPrenom() + " : l'identifiant "
                    + e.identifiant() + " est déjà utilisé.");
        }
    }

    private void gestionCollaborateurs() {}

    private void voirCollaborateurs() {}

    //endregion

    //region menuTickets

    private void menuTickets() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Gestion Tickets ===");
            System.out.println("1 - Créer un ticket");
            System.out.println("2 - Gestion des tickets");
            System.out.println("3 - Voir les tickets");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> creerTicket();
                case "2" -> gestionTickets();
                case "3" -> voirTickets();
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    private void creerTicket(){}

    private void gestionTickets(){}

    private void voirTickets(){}

    //endregion
}
