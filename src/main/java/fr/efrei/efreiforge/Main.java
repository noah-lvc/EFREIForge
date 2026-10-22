package fr.efrei.efreiforge;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory fabrique =
                Persistence.createEntityManagerFactory(
                        "collaborateurs-pu");

        Menu menu =
                new Menu(fabrique);

        menu.lancer();

        fabrique.close();
    }
}
