package br.ufscar.multiloja.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias", uniqueConstraints = @UniqueConstraint(columnNames = {"loja_id", "nome"}))
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "loja_id")
    private Loja loja;
    @Column(nullable = false)
    private String nome;
    private String descricao;

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
}
