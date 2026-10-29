package fr.efrei.efreiforge;

import java.util.Scanner;

import jakarta.persistence.*;


@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    String rue;

    @Column(length = 5)
    int codePostal;

    @Column(length = 50)
    String ville;

    @Column(length = 40)
    String pays;

    protected Adresse() { }

    public Adresse (String rue, int codePostal, String ville, String pays){
        this.rue = rue;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
    }

    public Adresse(Scanner scanner){
        System.out.println("Entrez votre rue :");
        String rue = scanner.nextLine();

        System.out.println("Entrez votre code postal :");
        int codePostal = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Entrez votre ville :");
        String ville = scanner.nextLine();

        System.out.println("Entrez votre pays :");
        String pays = scanner.nextLine();

        this(rue, codePostal, ville, pays);
    }

    public int getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }
    @Override
    public String toString() {
        return "Adresse{" +
                "rue='" + rue + '\'' +
                ", codePostal=" + codePostal +
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                '}';
    }
}
