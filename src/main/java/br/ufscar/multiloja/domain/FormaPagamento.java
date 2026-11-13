package br.ufscar.multiloja.domain;

public enum FormaPagamento {
    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito"),
    PIX("Pix"),
    BOLETO("Boleto");

    private final String rotulo;

    FormaPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
