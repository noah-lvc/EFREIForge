package fr.efrei.efreiforge;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;
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
            System.out.println("1 - Section Collaborateurs");
            System.out.println("2 - Section Tickets");
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

            System.out.println("\n=== Section Collaborateurs ===");
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
            collaborateurService.ajouterCollaborateur(collaborateur);
            System.out.println(collaborateur.getPrenom() + " (" + collaborateur.getIdentifiant() + ") a été ajouté(e).");
        } catch (CollaborateurDejaExistantException e) {
            System.out.println("Impossible d'ajouter " + collaborateur.getPrenom() + " : l'identifiant "
                    + e.identifiant() + " est déjà utilisé.");
        }
    }

    private void gestionCollaborateurs() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Gestion Collaborateurs ===");
            System.out.println("1 - Augmenter le salaire d'un collaborateur");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> augmenterSalaire();
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    public void augmenterSalaire(){
        System.out.println("Identifiant du collaborateur :");
        String id = scanner.nextLine();
        System.out.println("Pourcentage d'augmentation :");
        try {
            double pourcentage = Double.parseDouble(scanner.nextLine());
            if (collaborateurService.augmenterCollaborateur(id, pourcentage)) {
                System.out.println("Augmentation appliquée.");
            } else {
                System.out.println("Aucun collaborateur avec l'identifiant " + id + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Le pourcentage doit être un nombre.");
        } catch (IllegalArgumentException e) {
            System.out.println("Augmentation refusée : " + e.getMessage());
        }
    }

    private void voirCollaborateurs() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Voir les collaborateurs ===");
            System.out.println("1 - Afficher tous les collaborateurs");
            System.out.println("2 - Afficher  collaborateur par nom");
            System.out.println("3 - Afficher collaborateur par identifiant");
            System.out.println("4 - Afficher le nombre de collaborateurs");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> afficherListeCollaborateur(collaborateurService.getAllCollaorateurs());
                case "2" -> {
                    System.out.println("Nom (ou morceau du nom) :");
                    afficherListeCollaborateur(collaborateurService.rechercherParNom(scanner.nextLine()));
                }
                case "3" -> {
                    System.out.println("Identifiant recherché :");
                    String identifiant = scanner.nextLine();
                    Collaborateur trouve = collaborateurService.trouverCollaborateur(identifiant);
                    if (trouve == null) {
                        System.out.println("Aucun collaborateur avec l'identifiant " + identifiant + ".");
                    } else {
                        trouve.afficher();
                    }
                }
                case "4" -> System.out.println("Nombre de collaborateurs : " + collaborateurService.nombreCollaborateur());
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    //endregion

    //region menuTickets

    private void menuTickets() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Section Tickets ===");
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

    private void creerTicket() {
        Ticket ticket = new Ticket(scanner);
        ticketService.ajouterTicket(ticket);
        System.out.println("Le ticket " +ticket.getTitre() + " (" + ticket.getIdentifiant() + ") a été ajouté(e).");
    }

    private void gestionTickets() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Gestion Tickets ===");
            System.out.println("1 - Assigner un ticket");
            System.out.println("2 - Avancer status d'un ticket");
            System.out.println("3 - Reculer status d'un ticker");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> assignerTicket();
                case "2" -> System.out.println("en cours de développement");
                case "3" -> System.out.println("en cours de développement");
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    private void assignerTicket() {
        afficherListeTickets(ticketService.getAllTickets());
        System.out.println("Entrez l'id du ticket à assigner :");

        try {
            long ticketId = Long.parseLong(scanner.nextLine());
            Ticket ticket = ticketService.getTicketByIdentifiant(ticketId);

            if (ticket == null) {
                System.out.println("Erreur : Aucun ticket ne correspond à cet ID.");
                return;
            }

            afficherListeCollaborateur(collaborateurService.getAllCollaorateurs());
            System.out.println("Entrez l'id du collaborateur à assigner :");
            String collabId = scanner.nextLine();

            Collaborateur collaborateur = collaborateurService.trouverCollaborateur(collabId);

            if (collaborateur == null) {
                System.out.println("Erreur : Aucun collaborateur ne correspond à cet ID.");
                return;
            }

            try {
                ticket.assignerCollaborateur(collaborateur);
                ticketService.mettreAJourTicket(ticket);
                System.out.println("Le ticket a été assigné avec succès à " + collaborateur.getPrenom() + " !");
            } catch (AssignationImpossibleException e) {
                System.out.println("Assignation refusée : " + e.getMessage());
            }


        } catch (NumberFormatException e) {
            System.out.println("Erreur : L'ID du ticket doit être un nombre.");
        }
    }

    private void voirTickets() {
        boolean retour = false;

        while (!retour) {

            System.out.println("\n=== Voir les tickets ===");
            System.out.println("1 - Afficher tous les tickets");
            System.out.println("2 - Afficher par statut");
            System.out.println("0 - Retour");

            switch (scanner.nextLine()) {
                case "1" -> afficherListeTickets(ticketService.getAllTickets());
                case "2" -> voirTicketParStatut();
                case "0" -> retour = true;
                default -> System.out.println("Choix invalide");
            }
        }
    }

    private void voirTicketParStatut(){
        Statut statut = null;
        boolean choixValide = false;
        while (!choixValide) {
            System.out.println("Choisissez le statut :");
            System.out.println("(1) - A_FAIRE");
            System.out.println("(2) - EN_COURS");
            System.out.println("(3) - A_VALIDER");
            System.out.println("(4) - TERMINE");
            System.out.println("(5) - Retour");

            String choix = scanner.nextLine();

            switch (choix) {
                case "1" -> {
                    statut = Statut.A_FAIRE;
                    choixValide = true;
                }
                case "2" -> {
                    statut = Statut.EN_COURS;
                    choixValide = true;
                }
                case "3" -> {
                    statut = Statut.A_VALIDER;
                    choixValide = true;
                }
                case "4" -> {
                    statut = Statut.TERMINE;
                    choixValide = true;
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Choix invalide. Veuillez taper 0, 1, 2, 3 ou 4.\n");
            }
        }
        afficherListeTickets(ticketService.getTicketsByStatut(statut));
    }

    //endregion

    private static void afficherListeCollaborateur(List<Collaborateur> collaborateurs) {
        if (collaborateurs.isEmpty()) {
            System.out.println("Aucun collaborateur trouvé.");
        }
        for (Collaborateur c : collaborateurs) {
            System.out.print(c.getClass().getSimpleName() + " - ");
            c.afficher();
        }
    }

    private static void afficherListeTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("Aucun ticket trouvé.");
        }
        for (Ticket t : tickets) {
            System.out.println(t.toString());
        }
    }
}
