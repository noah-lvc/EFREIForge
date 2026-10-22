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

            afficherMenuPrincipal();

            switch (scanner.nextLine()) {
                case "1" -> menuCollaborateurs();
                case "2" -> menuTickets();
                case "0" -> continuer = false;


            }
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n=== EFREIForge ===");
        System.out.println("1 - Gestion Collaborateurs");
        System.out.println("2 - Gestion Tickets");
        System.out.println("0 - Quitter");
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

    private void creerCollaborateur() {}

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
