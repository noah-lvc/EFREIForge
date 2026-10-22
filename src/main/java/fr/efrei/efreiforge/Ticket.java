package fr.efrei.efreiforge;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Ticket {

    @Id
    @Column(length = 10)
    private double id;

    @Column(length = 100)
    private String titre;

    @Column(length = 50)
    private String priorite;

    public Ticket(){}

    public Ticket(String titre, String priorite) {
        this.titre = titre;
        this.priorite = priorite;
    }
}
