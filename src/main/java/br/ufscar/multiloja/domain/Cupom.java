package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "cupons")
public class Cupom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "loja_id")
    private Loja loja;
    /** RN005: o cupom só afeta produtos desta categoria. */
    @ManyToOne(optional = false) @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    @Column(nullable = false, unique = true)
    private String codigo;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoCupom tipo;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;
    private LocalDate validade;
    private boolean ativo = true;

    public boolean isUtilizavel() {
        return ativo && (validade == null || !validade.isBefore(LocalDate.now()));
    }

    /** Calcula o desconto sobre a parte do carrinho que pertence à categoria do cupom. */
    public BigDecimal calcularDesconto(BigDecimal baseElegivel) {
        if (baseElegivel == null || baseElegivel.signum() <= 0) return BigDecimal.ZERO;
        BigDecimal desconto = (tipo == TipoCupom.PERCENTUAL)
                ? baseElegivel.multiply(valor).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : valor;
        return desconto.min(baseElegivel).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoCupom getTipo() {
        return tipo;
    }

    public void setTipo(TipoCupom tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
