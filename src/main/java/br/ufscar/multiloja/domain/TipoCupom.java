package br.ufscar.multiloja.domain;

public enum TipoCupom {
    PERCENTUAL("Percentual (%)"),
    VALOR_FIXO("Valor fixo (R$)");

    private final String rotulo;

    TipoCupom(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
