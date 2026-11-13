package br.ufscar.multiloja.domain;

public enum TipoEndereco {
    RESIDENCIAL("Residencial"),
    COMERCIAL("Comercial"),
    OUTRO("Outro");

    private final String rotulo;

    TipoEndereco(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
