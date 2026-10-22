package fr.efrei.efreiforge;

import java.util.Scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Testeur")
public class Testeur extends Collaborateur {

    protected Testeur() { }

    public Testeur(String identifiant, String nom, String prenom, String langagePrefere, double salaire, String rue, int codePostal, String ville, String pays) {
        super(identifiant, nom, prenom, langagePrefere, salaire, rue, codePostal, ville, pays);
    }

    public Testeur (Scanner scanner){
        super(scanner);
    }

    @Override
    public void travailler() {
        System.out.println(prenom + " exécute une campagne de tests.");
    }
}
