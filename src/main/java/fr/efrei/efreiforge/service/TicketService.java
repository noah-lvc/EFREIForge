package fr.efrei.efreiforge.service;

import fr.efrei.efreiforge.modele.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

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

    public Ticket getTicketByIdentifiant(long identifiant) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.find(Ticket.class, identifiant);
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


    private long compterTickets(Collaborateur collaborateur) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                            "select count(t) from Ticket t " +
                                    "where t.collaborateur = :collaborateur " +
                                    "and t.statut = EN_COURS",
                            Long.class)
                    .setParameter("collaborateur", collaborateur)
                    .getSingleResult();
        }
    }

    public boolean supprimerTicket(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            try {
                Ticket t = em.find(Ticket.class, ticket.getIdentifiant());

                if (t == null || t.getStatut() != Statut.A_FAIRE) {
                    transaction.rollback();
                    return false;
                }

                em.remove(t);
                transaction.commit();
                return true;

            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void avancerTicket(Ticket ticket, Collaborateur acteur) {
        switch (ticket.getStatut()) {
            case A_FAIRE -> {
                Collaborateur programmeur = ticket.getCollaborateur();

                if (programmeur == null) {
                    throw new IllegalStateException("Le ticket doit être assigné avant d'être démarré.");
                }
                // On vérifie le programmeur assigné (et non l'acteur qui clique)
                if (compterTickets(programmeur) >= 3) {
                    throw new IllegalStateException("Le programmeur assigné possède déjà 3 tickets en cours.");
                }

                ticket.setStatut(Statut.EN_COURS);
            }
            case EN_COURS -> {
                ticket.setStatut(Statut.A_VALIDER);
            }
            case A_VALIDER -> {
                if (!(acteur instanceof Testeur)) {
                    throw new IllegalStateException("Seul un Testeur peut valider un ticket.");
                }
                ticket.setStatut(Statut.TERMINE);
            }
            case TERMINE -> {
                throw new IllegalStateException("Le ticket est déjà terminé, il ne peut plus avancer.");
            }
        }
    }

    public void reculerTicket(Ticket ticket, Collaborateur acteur) {
        switch (ticket.getStatut()) {
            case A_FAIRE, EN_COURS, TERMINE ->
                    throw new IllegalStateException("Le statut du ticket ne peut pas reculer.");

            case A_VALIDER -> {
                if (!(acteur instanceof Testeur)) {
                    throw new IllegalStateException("Seul un Testeur peut renvoyer un ticket.");
                }

                Collaborateur programmeur = ticket.getCollaborateur();

                // On s'assure que le programmeur qui va récupérer le ticket n'en a pas déjà 3 !
                if (programmeur != null && compterTickets(programmeur) >= 3) {
                    throw new IllegalStateException("Le programmeur assigné possède déjà 3 tickets en cours.");
                }

                ticket.setStatut(Statut.EN_COURS);
            }
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

    public void mettreAJourTicket(Ticket ticket) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.merge(ticket);
                transaction.commit();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

}
