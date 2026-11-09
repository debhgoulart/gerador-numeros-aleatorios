package br.com.geradornumeros.persistence;

import br.com.geradornumeros.exception.PersistenceException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JPAUtil {

    private static final Logger LOGGER = Logger.getLogger(JPAUtil.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "GeradorNumerosPU";

    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    private JPAUtil() {
    }

    public static synchronized void init() {
        if (emf == null || !emf.isOpen()) {
            try {
                LOGGER.info("Inicializando EntityManagerFactory para a unidade: " + PERSISTENCE_UNIT_NAME);
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                LOGGER.info("EntityManagerFactory inicializado com sucesso.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao inicializar EntityManagerFactory", e);
                throw new PersistenceException("Falha ao inicializar o provedor JPA/Hibernate", e);
            }
        }
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            init();
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        try {
            EntityManager em = getEntityManagerFactory().createEntityManager();
            threadLocalEntityManager.set(em);
            return em;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar EntityManager", e);
            throw new PersistenceException("Nao foi possivel criar o EntityManager", e);
        }
    }

    public static void setEntityManager(EntityManager em) {
        threadLocalEntityManager.set(em);
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em == null || !em.isOpen()) {
            em = createEntityManager();
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao fechar EntityManager da thread", e);
        } finally {
            threadLocalEntityManager.remove();
        }
    }

    public static synchronized void shutdown() {
        try {
            closeEntityManager();
            if (emf != null && emf.isOpen()) {
                LOGGER.info("Encerrando EntityManagerFactory...");
                emf.close();
                LOGGER.info("EntityManagerFactory encerrado com sucesso.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao encerrar EntityManagerFactory", e);
        } finally {
            emf = null;
        }
    }
}
