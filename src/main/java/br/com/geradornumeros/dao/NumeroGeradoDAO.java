package br.com.geradornumeros.dao;

import br.com.geradornumeros.exception.PersistenceException;
import br.com.geradornumeros.model.NumeroGerado;
import br.com.geradornumeros.persistence.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumeroGeradoDAO implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(NumeroGeradoDAO.class.getName());

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    public NumeroGerado salvar(NumeroGerado numeroGerado) {
        if (numeroGerado == null) {
            throw new IllegalArgumentException("O objeto NumeroGerado nao pode ser nulo.");
        }

        EntityManager em = getEntityManager();
        boolean localTransaction = false;

        try {
            // Se o filter nao iniciou a transacao, o DAO gerencia localmente
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                localTransaction = true;
            }

            if (numeroGerado.getId() == null) {
                em.persist(numeroGerado);
            } else {
                numeroGerado = em.merge(numeroGerado);
            }

            if (localTransaction) {
                em.getTransaction().commit();
            }

            LOGGER.info("Registro salvo com sucesso. ID: " + numeroGerado.getId());
            return numeroGerado;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar NumeroGerado: " + numeroGerado, e);
            if (localTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenceException("Erro ao persistir o numero gerado no banco de dados.", e);
        }
    }

    public List<NumeroGerado> buscarTodos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<NumeroGerado> query = em.createQuery(
                    "SELECT n FROM NumeroGerado n ORDER BY n.dataGeracao DESC", NumeroGerado.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao consultar historico de numeros gerados", e);
            throw new PersistenceException("Erro ao consultar o historico no banco de dados.", e);
        }
    }

    public NumeroGerado buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        EntityManager em = getEntityManager();
        try {
            return em.find(NumeroGerado.class, id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar NumeroGerado por ID: " + id, e);
            throw new PersistenceException("Erro ao consultar registro pelo identificador.", e);
        }
    }
}
