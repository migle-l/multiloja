package br.ufscar.multiloja.domain;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando pagamento"),
    PAGO("Pago"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue");

    private final String rotulo;

    StatusPedido(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
