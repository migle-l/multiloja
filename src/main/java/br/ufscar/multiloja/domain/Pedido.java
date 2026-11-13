package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String numero;
    @ManyToOne(optional = false) @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    /** RF010: todo pedido tem um endereço de entrega. */
    @ManyToOne(optional = false) @JoinColumn(name = "endereco_id")
    private Endereco endereco;
    @ManyToOne @JoinColumn(name = "cupom_id")
    private Cupom cupom;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();
    /** RN003: um pedido pode ter vários pagamentos. */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
    }

    public void adicionarPagamento(Pagamento pagamento) {
        pagamento.setPedido(this);
        pagamentos.add(pagamento);
    }

    public boolean contemLoja(Long lojaId) {
        return itens.stream().anyMatch(i -> i.getVariacao().getProduto().getLoja().getId().equals(lojaId));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
