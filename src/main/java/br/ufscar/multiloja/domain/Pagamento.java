package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    @Column(nullable = false, unique = true)
    private String codigo;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private FormaPagamento forma;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;
    @Column(nullable = false)
    private LocalDateTime dataTransacao = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
