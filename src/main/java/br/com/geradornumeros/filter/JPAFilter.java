package br.com.geradornumeros.filter;

import br.com.geradornumeros.persistence.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "JPAFilter", urlPatterns = {"*.xhtml", "/faces/*"})
public class JPAFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JPAFilter.class.getName());
    public static final String ENTITY_MANAGER_ATTR = "br.com.geradornumeros.EntityManager";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            LOGGER.info("Inicializando JPAFilter e EntityManagerFactory...");
            JPAUtil.init();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha critica ao inicializar EntityManagerFactory no JPAFilter", e);
            throw new ServletException("Erro na inicializacao do provedor JPA", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Cria e associa o EntityManager a requisicao e a thread
        EntityManager em = JPAUtil.getEntityManager();
        request.setAttribute(ENTITY_MANAGER_ATTR, em);

        EntityTransaction tx = null;
        boolean isPostRequest = "POST".equalsIgnoreCase(method);

        try {
            if (isPostRequest) {
                tx = em.getTransaction();
                if (!tx.isActive()) {
                    tx.begin();
                    LOGGER.fine("Transacao iniciada para requisicao POST: " + requestURI);
                }
            }

            chain.doFilter(request, response);

            if (tx != null && tx.isActive()) {
                tx.commit();
                LOGGER.fine("Transacao confirmada (commit) com sucesso para: " + requestURI);
            } else if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
                LOGGER.fine("Transacao iniciada na requisicao confirmada (commit) para: " + requestURI);
            }

        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Excecao capturada no JPAFilter para a requisicao: " + requestURI, t);
            try {
                if (tx != null && tx.isActive()) {
                    LOGGER.warning("Realizando rollback da transacao devido a erro.");
                    tx.rollback();
                } else if (em.isOpen() && em.getTransaction().isActive()) {
                    LOGGER.warning("Realizando rollback da transacao ativa devido a erro.");
                    em.getTransaction().rollback();
                }
            } catch (Exception rbEx) {
                LOGGER.log(Level.SEVERE, "Erro ao tentar realizar rollback da transacao", rbEx);
            }

            if (t instanceof ServletException) {
                throw (ServletException) t;
            } else if (t instanceof IOException) {
                throw (IOException) t;
            } else {
                throw new ServletException("Erro no processamento da requisicao JPAFilter", t);
            }
        } finally {
            JPAUtil.closeEntityManager();
            request.removeAttribute(ENTITY_MANAGER_ATTR);
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("Destruindo JPAFilter e encerrando recursos do EntityManagerFactory...");
        JPAUtil.shutdown();
    }
}
