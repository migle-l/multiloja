package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos", uniqueConstraints = @UniqueConstraint(columnNames = {"loja_id", "codigo"}))
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "loja_id")
    private Loja loja;
    @ManyToOne(optional = false) @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    @Column(nullable = false)
    private String codigo;
    @Column(nullable = false)
    private String nome;
    @Column(length = 2000)
    private String descricao;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoBase;
    /** Estoque geral do produto (o estoque vendável fica em cada variação). */
    private int estoque;
    private boolean ativo = true;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariacaoProduto> variacoes = new ArrayList<>();

    public int getEstoqueVariacoes() {
        return variacoes.stream().mapToInt(VariacaoProduto::getEstoque).sum();
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<VariacaoProduto> getVariacoes() {
        return variacoes;
    }

    public void setVariacoes(List<VariacaoProduto> variacoes) {
        this.variacoes = variacoes;
    }
}
