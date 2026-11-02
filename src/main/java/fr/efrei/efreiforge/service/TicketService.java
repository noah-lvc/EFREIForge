package fr.efrei.efreiforge.service;

import fr.efrei.efreiforge.modele.Collaborateur;
import fr.efrei.efreiforge.modele.Statut;
import fr.efrei.efreiforge.modele.Ticket;
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


    private long compterTickets(EntityManager em, Collaborateur collaborateur, Statut statut) {
        return em.createQuery(
                        "select count(t) from Ticket t " +
                                "where t.collaborateur = :collaborateur " +
                                "and t.statut = :statut",
                        Long.class)
                .setParameter("collaborateur", collaborateur)
                .setParameter("statut", statut)
                .getSingleResult();
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

    public boolean demarrerTicket(long identifiant) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            try {
                Ticket ticket = em.find(Ticket.class, identifiant);

                if (ticket == null || ticket.getCollaborateur() == null) {
                    transaction.rollback();
                    return false;
                }

                long nombre = compterTickets(em, ticket.getCollaborateur(), Statut.EN_COURS);

                boolean demarre = ticket.demarrer(nombre);

                if (demarre == false) {
                    transaction.rollback();
                    return false;
                }

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



    public boolean soumettreTicketValidation(long identifiant) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Ticket ticket = em.find(Ticket.class, identifiant);
                if (ticket == null) {
                    transaction.rollback();
                    return false;
                }

                if (!ticket.soumettreValidation()) {
                    transaction.rollback();
                    return false;
                }
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

    public boolean validerTicket(long identifiant, String identifiantTesteur) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Ticket ticket = em.find(Ticket.class, identifiant);
                if (ticket == null || identifiantTesteur == null || identifiantTesteur.isBlank()) {
                    transaction.rollback();
                    return false;
                }
                Collaborateur validateur = em.find(Collaborateur.class, identifiantTesteur);

                if (!ticket.valider(validateur)) {
                    transaction.rollback();
                    return false;
                }
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

    public boolean renvoyerTicketEnCours(long identifiant, String identifiantTesteur) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Ticket ticket = em.find(Ticket.class, identifiant);
                if (ticket == null || ticket.getCollaborateur() == null
                        || identifiantTesteur == null || identifiantTesteur.isBlank()) {
                    transaction.rollback();
                    return false;
                }
                Collaborateur validateur = em.find(Collaborateur.class, identifiantTesteur);
                long nombre = compterTickets(em, ticket.getCollaborateur(), Statut.EN_COURS);

                if (!ticket.renvoyerEnCours(validateur, nombre)) {
                    transaction.rollback();
                    return false;
                }
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
