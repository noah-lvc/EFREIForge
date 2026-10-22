package fr.efrei.efreiforge;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titre;

    @Enumerated(EnumType.STRING)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    public Ticket(){}

    public Ticket(String titre, Priorite priorite) {
        this.titre = titre;
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
    }
}
