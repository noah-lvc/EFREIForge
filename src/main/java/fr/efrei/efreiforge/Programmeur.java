package fr.efrei.efreiforge;

import java.util.Scanner;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Programmeur")
public class Programmeur extends Collaborateur implements Formateur {

    protected Programmeur() { }

    public Programmeur(String identifiant, String nom, String prenom, String langagePrefere, double salaire, String rue, int codePostal, String ville, String pays) {
        super(identifiant, nom, prenom, langagePrefere, salaire, rue, codePostal, ville, pays);
    }

    public Programmeur(Scanner scanner){
        super(scanner);
    }

    @Override
    public void travailler() {
        System.out.println(prenom + " développe une fonctionnalité.");
    }

    @Override
    public void former(){
        System.out.println("je suis formateur");
    }
}

