package fr.efrei.efreiforge.service;

import fr.efrei.efreiforge.modele.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

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
                    logger.warn("Suppression refusée pour le ticket {} (Statut incorrect ou introuvable)", ticket.getIdentifiant());
                    return false;
                }

                em.remove(t);
                transaction.commit();
                logger.info("Ticket {} supprimé de la base", ticket.getIdentifiant());
                return true;

            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Erreur lors de la suppression du ticket {}", ticket.getIdentifiant(), e);
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

                // On s'assure que le programmeur qui va récupérer le ticket n'en a pas déjà 3
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
                logger.info("Nouveau ticket enregistré en base (Titre : {})", ticket.getTitre());
            } catch (RuntimeException e) {
                transaction.rollback();
                logger.error("Erreur lors de la création du ticket", e);
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
                logger.info("Ticket {} mis à jour avec succès", ticket.getIdentifiant());
            } catch (RuntimeException e) {
                transaction.rollback();
                logger.error("Erreur lors de la mise à jour du ticket {}", ticket.getIdentifiant(), e);
                throw e;
            }
        }
    }

}
