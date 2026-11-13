package br.ufscar.multiloja.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** RN004: a avaliação só existe ligada a um item de pedido. */
    @OneToOne(optional = false) @JoinColumn(name = "item_pedido_id", unique = true)
    private ItemPedido itemPedido;
    private int nota;
    @Column(length = 1000)
    private String comentario;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPedido getItemPedido() {
        return itemPedido;
    }

    public void setItemPedido(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
