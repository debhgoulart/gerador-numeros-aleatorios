package br.com.geradornumeros.controller;

import br.com.geradornumeros.dao.NumeroGeradoDAO;
import br.com.geradornumeros.model.NumeroGerado;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "numeroBean")
@ViewScoped
public class NumeroBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(NumeroBean.class.getName());

    private Integer valorMaximo;
    private NumeroGerado numeroGerado;
    private Long idParam;
    private List<NumeroGerado> historico;

    private NumeroGeradoDAO numeroGeradoDAO;
    private Random random;

    @PostConstruct
    public void init() {
        this.numeroGeradoDAO = new NumeroGeradoDAO();
        this.random = new Random();
    }

    public String gerar() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (this.valorMaximo == null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Campo obrigatorio", "Por favor, informe o valor maximo.");
            context.addMessage(null, message);
            return null;
        }

        if (this.valorMaximo < 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Valor invalido", "O valor maximo deve ser maior ou igual a zero (>= 0).");
            context.addMessage(null, message);
            return null;
        }

        try {
            int aleatorio;
            if (this.valorMaximo == Integer.MAX_VALUE) {
                aleatorio = (int) (random.nextDouble() * ((double) Integer.MAX_VALUE + 1.0));
            } else {
                aleatorio = random.nextInt(this.valorMaximo + 1);
            }

            NumeroGerado novoRegistro = new NumeroGerado(this.valorMaximo, aleatorio, new Date());

            this.numeroGerado = numeroGeradoDAO.salvar(novoRegistro);

            LOGGER.info("Numero aleatorio gerado com sucesso: " + aleatorio +
                    " (Limite: " + this.valorMaximo + ", ID: " + this.numeroGerado.getId() + ")");

            context.getExternalContext().getFlash().put("numeroGerado", this.numeroGerado);

            return "resultado?faces-redirect=true&id=" + this.numeroGerado.getId();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar geracao e persistencia do numero", e);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro ao gerar numero", "Ocorreu um erro interno ao salvar o registro no banco de dados.");
            context.addMessage(null, message);
            return null;
        }
    }

    public void inicializarResultado() {
        FacesContext context = FacesContext.getCurrentInstance();

        Object flashObj = context.getExternalContext().getFlash().get("numeroGerado");
        if (flashObj instanceof NumeroGerado) {
            this.numeroGerado = (NumeroGerado) flashObj;
            return;
        }

        if (this.numeroGerado == null && this.idParam != null) {
            try {
                this.numeroGerado = numeroGeradoDAO.buscarPorId(this.idParam);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Falha ao buscar registro por idParam: " + this.idParam, e);
            }
        }
    }

    public void carregarHistorico() {
        try {
            this.historico = numeroGeradoDAO.buscarTodos();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar lista de historico", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nao foi possivel carregar o historico."));
        }
    }

    public String novo() {
        this.valorMaximo = null;
        this.numeroGerado = null;
        return "index?faces-redirect=true";
    }

    public String verHistorico() {
        return "historico?faces-redirect=true";
    }

    public Integer getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Integer valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public NumeroGerado getNumeroGerado() {
        return numeroGerado;
    }

    public void setNumeroGerado(NumeroGerado numeroGerado) {
        this.numeroGerado = numeroGerado;
    }

    public Long getIdParam() {
        return idParam;
    }

    public void setIdParam(Long idParam) {
        this.idParam = idParam;
    }

    public List<NumeroGerado> getHistorico() {
        if (historico == null) {
            carregarHistorico();
        }
        return historico;
    }

    public void setHistorico(List<NumeroGerado> historico) {
        this.historico = historico;
    }
}
