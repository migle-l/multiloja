package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrinhos")
public class Carrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** RN001: um único carrinho por cliente. */
    @OneToOne(optional = false) @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;
    @ManyToOne @JoinColumn(name = "cupom_id")
    private Cupom cupom;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    public boolean isVazio() {
        return itens.isEmpty();
    }

    public int quantidadeDe(VariacaoProduto variacao) {
        return itens.stream()
                .filter(i -> i.getVariacao().getId().equals(variacao.getId()))
                .mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    /** RN002: o item sempre aponta para uma variação específica. */
    public void adicionar(VariacaoProduto variacao, int quantidade) {
        for (ItemCarrinho item : itens) {
            if (item.getVariacao().getId().equals(variacao.getId())) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        ItemCarrinho novo = new ItemCarrinho();
        novo.setCarrinho(this);
        novo.setVariacao(variacao);
        novo.setQuantidade(quantidade);
        itens.add(novo);
    }

    public BigDecimal getSubtotal() {
        return itens.stream().map(ItemCarrinho::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal baseElegivelPara(Cupom c) {
        return itens.stream()
                .filter(i -> i.getVariacao().getProduto().getCategoria().getId().equals(c.getCategoria().getId()))
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDesconto() {
        if (cupom == null || !cupom.isUtilizavel()) return BigDecimal.ZERO;
        return cupom.calcularDesconto(baseElegivelPara(cupom));
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(getDesconto());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }
}
