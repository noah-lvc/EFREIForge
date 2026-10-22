package fr.efrei.efreiforge;


import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Scanner;

public class HelloEfrei {
    public static void main(String[] args) {

        EntityManagerFactory fabrique = Persistence.createEntityManagerFactory("collaborateurs-pu");
        CollaborateurService service = new CollaborateurService(fabrique);
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {
            System.out.println("1 - Ajouter Alice (C001)");
            System.out.println("2 - Afficher le nombre de collaborateurs");
            System.out.println("3 - Rechercher un collaborateur par identifiant");
            System.out.println("4 - Augmenter le salaire d'un collaborateur");
            System.out.println("5 - Collaborateurs dont le salaire dépasse un seuil");
            System.out.println("6 - Rechercher des collaborateurs par nom");
            System.out.println("7 - Ajouter un Collaborateur");
            System.out.println("8 - Afficher tous les collaborateurs");
            System.out.println("0 - Quitter");
            String choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    Programmeur alice = new Programmeur("C001", "Martin", "Alice", "Java",
                            48000, "2 rue des carrières", 92220, "Bagneux", "France");
                    try {
                        service.ajouter(alice);
                        System.out.println("Alice (C001) a été ajoutée.");
                    } catch (CollaborateurDejaExistantException e) {
                        System.out.println("Impossible d'ajouter Alice : l'identifiant "
                                + e.identifiant() + " est déjà utilisé.");
                    }
                    break;
                case "2":
                    System.out.println("Nombre de collaborateurs : " + service.nombre());
                    break;
                case "3":
                    System.out.println("Identifiant recherché :");
                    String identifiant = scanner.nextLine();
                    Collaborateur trouve = service.trouver(identifiant);
                    if (trouve == null) {
                        System.out.println("Aucun collaborateur avec l'identifiant " + identifiant + ".");
                    } else {
                        trouve.afficher();
                    }
                    break;
                case "4":
                    System.out.println("Identifiant du collaborateur :");
                    String id = scanner.nextLine();
                    System.out.println("Pourcentage d'augmentation :");
                    try {
                        double pourcentage = Double.parseDouble(scanner.nextLine());
                        if (service.augmenter(id, pourcentage)) {
                            System.out.println("Augmentation appliquée.");
                        } else {
                            System.out.println("Aucun collaborateur avec l'identifiant " + id + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Le pourcentage doit être un nombre.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Augmentation refusée : " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.println("Seuil de salaire :");
                    try {
                        double seuil = Double.parseDouble(scanner.nextLine());
                        afficherListe(service.salaireSuperieurA(seuil));
                    } catch (NumberFormatException e) {
                        System.out.println("Le seuil doit être un nombre.");
                    }
                    break;
                case "6":
                    System.out.println("Nom (ou morceau du nom) :");
                    afficherListe(service.rechercherParNom(scanner.nextLine()));
                    break;
                case "7":
                    System.out.println("Quel type de collaborateur voulez-vous ajouter ?");
                    System.out.println("1 - Programmeur");
                    System.out.println("2 - Testeur");
                    String type = scanner.nextLine();

                    Collaborateur collaborateur;

                    if (type.equals("1")) {
                        collaborateur = new Programmeur(scanner);
                    } else if (type.equals("2")) {
                        collaborateur = new Testeur(scanner);
                    } else {
                        System.out.println("Choix invalide.");
                        break;
                    }
                    try {
                        service.ajouter(collaborateur);
                        System.out.println(collaborateur.getPrenom() + " (" + collaborateur.getIdentifiant() + ") à été ajoutée.");
                    } catch (CollaborateurDejaExistantException e) {
                        System.out.println("Impossible d'ajouter " + collaborateur.getPrenom() + " : l'identifiant "
                                + e.identifiant() + " est déjà utilisé.");
                    }
                    break;

                case "8":
                    afficherListe(service.getAllCollaorateurs());
                    break;

                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix inconnu.");
            }
        }
        fabrique.close();
    }

    private static void afficherListe(List<Collaborateur> collaborateurs) {
        if (collaborateurs.isEmpty()) {
            System.out.println("Aucun collaborateur trouvé.");
        }
        for (Collaborateur c : collaborateurs) {
            System.out.print(c.getClass().getSimpleName() + " " + c.getIdentifiant() + " - ");
            c.afficher();
        }
    }
}

