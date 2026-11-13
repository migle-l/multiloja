package br.ufscar.multiloja.form;

import jakarta.validation.constraints.NotBlank;

public class CategoriaForm {
    @NotBlank(message = "Informe o nome da categoria")
    private String nome;
    private String descricao;

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
