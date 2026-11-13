package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "variacoes_produto")
public class VariacaoProduto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "produto_id")
    private Produto produto;
    private String cor;
    private String tamanho;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoAdicional = BigDecimal.ZERO;
    private int estoque;
    /** Controle otimista: evita vender o mesmo estoque duas vezes em compras simultâneas. */
    @Version
    private Long versao;

    public BigDecimal getPrecoFinal() {
        return produto.getPrecoBase().add(precoAdicional);
    }

    public String getDescricao() {
        List<String> partes = new ArrayList<>();
        if (cor != null && !cor.isBlank()) partes.add(cor);
        if (tamanho != null && !tamanho.isBlank()) partes.add(tamanho);
        return String.join(" / ", partes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public BigDecimal getPrecoAdicional() {
        return precoAdicional;
    }

    public void setPrecoAdicional(BigDecimal precoAdicional) {
        this.precoAdicional = precoAdicional;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }
}
