package br.ufscar.multiloja.form;

import br.ufscar.multiloja.domain.Produto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProdutoForm {
    @NotBlank(message = "Informe o código do produto")
    private String codigo;
    @NotBlank(message = "Informe o nome")
    private String nome;
    private String descricao;
    @NotNull(message = "Informe o preço") @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal precoBase;
    @NotNull(message = "Informe o estoque") @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque = 0;
    @NotNull(message = "Escolha uma categoria")
    private Long categoriaId;
    private boolean ativo = true;

    public static ProdutoForm de(Produto p) {
        ProdutoForm f = new ProdutoForm();
        f.codigo = p.getCodigo();
        f.nome = p.getNome();
        f.descricao = p.getDescricao();
        f.precoBase = p.getPrecoBase();
        f.estoque = p.getEstoque();
        f.categoriaId = p.getCategoria().getId();
        f.ativo = p.isAtivo();
        return f;
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

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
