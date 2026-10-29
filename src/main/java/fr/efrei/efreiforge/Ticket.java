package fr.efrei.efreiforge;

import jakarta.persistence.*;

import java.util.Scanner;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long identifiant;

    private String titre;

    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "assigne_id")
    private Collaborateur collaborateur;


    public Ticket(){}

    public Ticket(String titre, Priorite priorite) {
        this.titre = titre;
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
    }

    public Ticket(Scanner scanner) {
        System.out.println("Entrez le titre du ticket :");
        String titre = scanner.nextLine();
        Priorite priorite = null;
        boolean choixValide = false;
        while (!choixValide) {
            System.out.println("Choisissez la priorité :");
            System.out.println("(1) - BASSE");
            System.out.println("(2) - MOYENNE");
            System.out.println("(3) - HAUTE");

            String choix = scanner.nextLine();

            switch (choix) {
                case "1" -> {
                    priorite = Priorite.BASSE;
                    choixValide = true; 
                }
                case "2" -> {
                    priorite = Priorite.NORMALE;
                    choixValide = true;
                }
                case "3" -> {
                    priorite = Priorite.HAUTE;
                    choixValide = true;
                }
                default -> System.out.println("Choix invalide. Veuillez taper 1, 2 ou 3.\n");
            }
        }

        this(titre, priorite);
    }


    public void assigner(Collaborateur collaborateur) {
        if (collaborateur == null) {
            throw new AssignationImpossibleException("Le collaborateur est obligatoire");
        }
        if (!(collaborateur instanceof Programmeur)) {
            throw new AssignationImpossibleException("Seul un programmeur peut être assigné");
        }
        if (this.statut != Statut.A_FAIRE) {
            throw new AssignationImpossibleException("Seul un ticket à faire peut être assigné");
        }

        this.collaborateur = collaborateur;
    }

    public long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(long identifiant) {
        this.identifiant = identifiant;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Collaborateur getCollaborateur() {
        return collaborateur;
    }

    public void setCollaborateur(Collaborateur collaborateur) {
        this.collaborateur = collaborateur;
    }

    @Override
    public String toString() {
        // On vérifie si un collaborateur est assigné pour afficher son nom, sinon on met "Non assigné"
        String infoCollaborateur = (collaborateur != null)
                ? collaborateur.getPrenom() + " " + collaborateur.getNom()
                : "Non assigné";

        return "ID : " + identifiant +
                " | Titre : " + titre +
                " | Priorité : " + priorite +
                " | Statut : " + statut +
                " | Assigné à : " + infoCollaborateur;
    }
}
