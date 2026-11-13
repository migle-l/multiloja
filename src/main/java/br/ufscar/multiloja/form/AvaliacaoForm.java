package br.ufscar.multiloja.form;

import jakarta.validation.constraints.*;

public class AvaliacaoForm {
    @NotNull
    private Long itemPedidoId;
    @NotNull(message = "Escolha uma nota") @Min(value = 1, message = "Nota mínima: 1") @Max(value = 5, message = "Nota máxima: 5")
    private Integer nota;
    @Size(max = 1000, message = "Máximo de 1000 caracteres")
    private String comentario;

    public Long getItemPedidoId() {
        return itemPedidoId;
    }

    public void setItemPedidoId(Long itemPedidoId) {
        this.itemPedidoId = itemPedidoId;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
