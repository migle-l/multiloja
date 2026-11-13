package br.ufscar.multiloja.form;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** Dados do checkout: endereço de entrega e até dois pagamentos (RN003). */
public class CheckoutForm {
    @NotNull(message = "Selecione o endereço de entrega")
    private Long enderecoId;
    private List<PagamentoForm> pagamentos = new ArrayList<>(List.of(new PagamentoForm(), new PagamentoForm()));

    /** Pagamentos efetivamente preenchidos (forma e valor positivo). */
    public List<PagamentoForm> pagamentosInformados() {
        return pagamentos.stream()
                .filter(p -> p.getForma() != null && p.getValor() != null && p.getValor().signum() > 0)
                .toList();
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }

    public List<PagamentoForm> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoForm> pagamentos) {
        this.pagamentos = pagamentos;
    }
}
