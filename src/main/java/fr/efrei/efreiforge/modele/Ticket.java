package fr.efrei.efreiforge.modele;

import fr.efrei.efreiforge.exception.AssignationImpossibleException;
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
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du ticket est obligatoire.");
        }
        this.titre = titre;
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
    }

    public void assignerCollaborateur(Collaborateur collaborateur) {
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
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du ticket est obligatoire.");
        }
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

    @Override
    public String toString() {
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
