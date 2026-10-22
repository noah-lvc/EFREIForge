package fr.efrei.efreiforge;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CollaborateurService {

    private static final Logger logger = LoggerFactory.getLogger(CollaborateurService.class);

    private final EntityManagerFactory fabrique;

    public CollaborateurService(EntityManagerFactory fabrique) {
        this.fabrique = fabrique;
    }

    public void ajouter(Collaborateur collaborateur) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                if (em.find(Collaborateur.class, collaborateur.getIdentifiant()) != null) {
                    logger.warn("Doublon refusé pour {}", collaborateur.getIdentifiant());
                    throw new CollaborateurDejaExistantException(collaborateur.getIdentifiant());
                }
                em.persist(collaborateur);
                transaction.commit();
                logger.info("Collaborateur {} enregistré en base", collaborateur.getIdentifiant());
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public List<Collaborateur> getAllCollaorateurs() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery("select c from Collaborateur c", Collaborateur.class)
                    .getResultList();
        }
    }


    public boolean augmenter(String identifiant, double pourcentage) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Collaborateur collaborateur = em.find(Collaborateur.class, identifiant);
                if (collaborateur == null) {
                    transaction.rollback();
                    return false;
                }
                collaborateur.augmentation(pourcentage);
                transaction.commit();
                logger.info("Augmentation de {} % pour {}", pourcentage, identifiant);
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public Collaborateur trouver(String identifiant) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.find(Collaborateur.class, identifiant);
        }
    }

    public long nombre() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery("select count(c) from Collaborateur c", Long.class)
                    .getSingleResult();
        }
    }

    public List<Collaborateur> salaireSuperieurA(double seuil) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where c.salaire > :seuil",
                            Collaborateur.class)
                    .setParameter("seuil", seuil)
                    .getResultList();
        }
    }

    public List<Collaborateur> rechercherParNom(String fragment) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where lower(c.nom) like :motif",
                            Collaborateur.class)
                    .setParameter("motif", "%" + fragment.toLowerCase() + "%")
                    .getResultList();
        }
    }
}
