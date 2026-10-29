package fr.efrei.efreiforge;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

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

    public List<Ticket> getTicketsByStatut(Statut statut) {
        try (EntityManager em = fabrique.createEntityManager()) {

            return em.createQuery(
                            "select t from Ticket t where t.statut = :statut",
                            Ticket.class
                    )
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    public List<Ticket> getTicketsByCollaborateur(Collaborateur collaborateur) {
        try (EntityManager em = fabrique.createEntityManager()) {

            return em.createQuery(
                            "select t from Ticket t where t.collaborateur.identifiant = :identifiant",
                            Ticket.class
                    )
                    .setParameter("identifiant", collaborateur.getIdentifiant())
                    .getResultList();
        }
    }

    public void ajouterTicket(Ticket ticket) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.persist(ticket);
                transaction.commit();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

}
