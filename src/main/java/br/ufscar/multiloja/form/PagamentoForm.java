package br.ufscar.multiloja.form;

import br.ufscar.multiloja.domain.FormaPagamento;
import java.math.BigDecimal;

public class PagamentoForm {
    private FormaPagamento forma;
    private BigDecimal valor;

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
