package fr.efrei.efreiforge;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private final EntityManagerFactory fabrique;

    public TicketService(EntityManagerFactory fabrique) {
        this.fabrique = fabrique;
    }

    public List<Ticket> getAllTickets() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery("select t from Ticket t", Ticket.class)
                    .getResultList();
        }
    }
}
