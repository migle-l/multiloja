package br.ufscar.multiloja.domain;

public enum StatusPagamento {
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado");

    private final String rotulo;

    StatusPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
