package br.ufscar.multiloja.form;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class VariacaoForm {
    private String cor;
    private String tamanho;
    @NotNull(message = "Informe o preço adicional (0 se não houver)") @DecimalMin(value = "0.00", message = "Não pode ser negativo")
    private BigDecimal precoAdicional = BigDecimal.ZERO;
    @NotNull(message = "Informe o estoque") @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque = 0;

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

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }
}
