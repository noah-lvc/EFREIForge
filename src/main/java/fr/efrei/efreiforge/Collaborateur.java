package fr.efrei.efreiforge;

import java.util.Scanner;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "metier", length = 40)
public abstract class Collaborateur {

    protected static final Logger logger = LoggerFactory.getLogger(Collaborateur.class);

    @Id
    @Column(length = 10)
    protected String identifiant;

    @Column(nullable = false, length = 50)
    protected String nom;

    @Column(length = 50)
    protected String prenom;

    @Column(length = 40)
    protected String langagePrefere;

    @Column(nullable = false)
    protected double salaire;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adresse_id")
    protected Adresse adresse;

    protected Collaborateur() { }

    public Collaborateur(String identifiant, String nom, String prenom, String langagePrefere, double salaire, Adresse adresse) {
        verifierNom(nom);
        verifierSalaire(salaire);

        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.langagePrefere = langagePrefere;
        this.salaire = salaire;
        this.adresse = adresse;
    }

    public Collaborateur(String identifiant, String nom, String prenom, String langagePrefere, double salaire, String rue, int codePostal, String ville, String pays) {
        this(identifiant, nom, prenom, langagePrefere, salaire, new fr.efrei.efreiforge.Adresse(rue, codePostal, ville, pays));
    }

    public Collaborateur(Scanner scanner){
        System.out.println("Entrez votre identifiant");
        String identifiant = scanner.nextLine();

        System.out.println("Entrez votre le nom");
        String nom = scanner.nextLine();

        System.out.println("Entrez votre le prenom");
        String prenom = scanner.nextLine();

        System.out.println("Entrez votre language préféré");
        String languePreferee = scanner.nextLine();

        System.out.println("Entrez votre salaire");
        double salaire = scanner.nextDouble();
        scanner.nextLine();

        this(identifiant, nom, prenom, languePreferee, salaire, new Adresse(scanner));
    }

    private void verifierNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
    }

    private void verifierSalaire(double salaire){
        if (salaire < 0) {
            throw new IllegalArgumentException("Le salaire ne peut pas être négatif");
        }
    }

    public void afficher() {
        System.out.println(
                "ID : " + identifiant +
                        " | Nom : " + nom +
                        " | Prénom : " + prenom +
                        " | Langage : " + langagePrefere +
                        " | Salaire : " + salaire
        );
    }

    public void augmentation(double augmentation) {
        if ((salaire * (1 + (augmentation / 100))) < 0) {
            throw new IllegalArgumentException("L'augmentation rendrait le salaire négatif");
        }
        double avant = this.salaire;
        this.salaire = this.salaire * (1 + (augmentation / 100));
        double apres = this.salaire;
        logger.debug("Salaire : {} -> {}", avant, apres);             // détail pour l'analyse
    }

    public abstract void travailler();

    public double getSalaire() {
        return salaire;
    }

    public String getIdentifiant(){
        return identifiant;
    }
    public void setSalaire(double salaire) {
        verifierSalaire(salaire);
        this.salaire = salaire;
    }

    public String getLangagePrefere() {
        return langagePrefere;
    }

    public void setLangagePrefere(String langagePrefere) {
        this.langagePrefere = langagePrefere;
    }

    public String getPrenom() {
        return prenom;
    }



    public void setPrenom(String prenom) {
        verifierNom(prenom);
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        verifierNom(nom);
        this.nom = nom;
    }

    public void setIdentifiant(String id) {
        this.identifiant = id;
    }
}

