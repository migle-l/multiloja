package br.ufscar.multiloja.form;

import br.ufscar.multiloja.domain.TipoCupom;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class CupomForm {
    @NotBlank(message = "Informe o código do cupom") @Size(min = 3, message = "Use ao menos 3 caracteres")
    private String codigo;
    @NotNull(message = "Escolha a categoria")
    private Long categoriaId;
    @NotNull(message = "Escolha o tipo")
    private TipoCupom tipo = TipoCupom.PERCENTUAL;
    @NotNull(message = "Informe o valor") @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate validade;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public TipoCupom getTipo() {
        return tipo;
    }

    public void setTipo(TipoCupom tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }
}
