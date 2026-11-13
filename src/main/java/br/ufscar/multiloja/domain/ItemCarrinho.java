package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_carrinho", uniqueConstraints = @UniqueConstraint(columnNames = {"carrinho_id", "variacao_id"}))
public class ItemCarrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "carrinho_id")
    private Carrinho carrinho;
    @ManyToOne(optional = false) @JoinColumn(name = "variacao_id")
    private VariacaoProduto variacao;
    private int quantidade;

    public BigDecimal getSubtotal() {
        return variacao.getPrecoFinal().multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public VariacaoProduto getVariacao() {
        return variacao;
    }

    public void setVariacao(VariacaoProduto variacao) {
        this.variacao = variacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
