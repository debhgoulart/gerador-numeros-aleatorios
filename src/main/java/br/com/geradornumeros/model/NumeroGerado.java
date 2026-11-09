package br.com.geradornumeros.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "numero_gerado")
public class NumeroGerado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "valor_maximo", nullable = false)
    private Integer valorMaximo;

    @Column(name = "numero_aleatorio", nullable = false)
    private Integer numeroAleatorio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_geracao", nullable = false)
    private Date dataGeracao;

    public NumeroGerado() {
    }

    public NumeroGerado(Integer valorMaximo, Integer numeroAleatorio, Date dataGeracao) {
        this.valorMaximo = valorMaximo;
        this.numeroAleatorio = numeroAleatorio;
        this.dataGeracao = dataGeracao != null ? (Date) dataGeracao.clone() : new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Integer valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Integer getNumeroAleatorio() {
        return numeroAleatorio;
    }

    public void setNumeroAleatorio(Integer numeroAleatorio) {
        this.numeroAleatorio = numeroAleatorio;
    }

    public Date getDataGeracao() {
        return dataGeracao != null ? (Date) dataGeracao.clone() : null;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao != null ? (Date) dataGeracao.clone() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumeroGerado that = (NumeroGerado) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NumeroGerado{" +
                "id=" + id +
                ", valorMaximo=" + valorMaximo +
                ", numeroAleatorio=" + numeroAleatorio +
                ", dataGeracao=" + dataGeracao +
                '}';
    }
}
