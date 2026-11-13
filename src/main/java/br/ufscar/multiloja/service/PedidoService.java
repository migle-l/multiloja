package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.*;
import br.ufscar.multiloja.form.CheckoutForm;
import br.ufscar.multiloja.form.PagamentoForm;
import br.ufscar.multiloja.repository.ClienteRepository;
import br.ufscar.multiloja.repository.EnderecoRepository;
import br.ufscar.multiloja.repository.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF009–RF011: conversão do carrinho em pedido, endereço de entrega e pagamentos. */
@Service
public class PedidoService {
    private final PedidoRepository pedidos;
    private final EnderecoRepository enderecos;
    private final ClienteRepository clientes;
    private final CarrinhoService carrinhos;

    public PedidoService(PedidoRepository pedidos, EnderecoRepository enderecos,
                         ClienteRepository clientes, CarrinhoService carrinhos) {
        this.pedidos = pedidos;
        this.enderecos = enderecos;
        this.clientes = clientes;
        this.carrinhos = carrinhos;
    }

    @Transactional
    public Pedido finalizar(Long clienteId, CheckoutForm form) {
        Carrinho carrinho = carrinhos.obter(clienteId);
        if (carrinho.isVazio()) throw new RegraNegocioException("Seu carrinho está vazio.");

        Endereco endereco = enderecos.findByIdAndClienteId(form.getEnderecoId(), clienteId)
                .filter(Endereco::isAtivo)
                .orElseThrow(() -> new RegraNegocioException("Selecione um endereço de entrega válido."));

        for (ItemCarrinho item : carrinho.getItens()) {
            VariacaoProduto v = item.getVariacao();
            if (!v.getProduto().isAtivo() || item.getQuantidade() > v.getEstoque()) {
                throw new RegraNegocioException("Estoque insuficiente para " + v.getProduto().getNome()
                        + " (" + v.getDescricao() + "). Ajuste o carrinho.");
            }
        }

        List<PagamentoForm> informados = form.pagamentosInformados();
        if (informados.isEmpty()) throw new RegraNegocioException("Informe ao menos uma forma de pagamento.");

        BigDecimal subtotal = carrinho.getSubtotal();
        BigDecimal desconto = carrinho.getDesconto();
        BigDecimal total = carrinho.getTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal soma = informados.stream().map(PagamentoForm::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        if (soma.compareTo(total) != 0) {
            throw new RegraNegocioException("A soma dos pagamentos (R$ " + soma.toPlainString()
                    + ") deve ser igual ao total do pedido (R$ " + total.toPlainString() + ").");
        }

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pedido.setCliente(clientes.getReferenceById(clienteId));
        pedido.setEndereco(endereco);
        if (carrinho.getCupom() != null && carrinho.getCupom().isUtilizavel()) pedido.setCupom(carrinho.getCupom());
        pedido.setSubtotal(subtotal);
        pedido.setDesconto(desconto);
        pedido.setTotal(total);

        for (ItemCarrinho item : carrinho.getItens()) {
            VariacaoProduto v = item.getVariacao();
            v.setEstoque(v.getEstoque() - item.getQuantidade()); // baixa de estoque (@Version protege contra concorrência)
            ItemPedido ip = new ItemPedido();
            ip.setVariacao(v);
            ip.setQuantidade(item.getQuantidade());
            ip.setPrecoUnitario(v.getPrecoFinal());
            pedido.adicionarItem(ip);
        }

        for (PagamentoForm pf : informados) {
            Pagamento pg = new Pagamento();
            pg.setCodigo("PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            pg.setForma(pf.getForma());
            pg.setValor(pf.getValor().setScale(2, RoundingMode.HALF_UP));
            // Simulação: cartão é aprovado na hora; Pix e boleto ficam pendentes até a loja confirmar.
            boolean cartao = pf.getForma() == FormaPagamento.CARTAO_CREDITO || pf.getForma() == FormaPagamento.CARTAO_DEBITO;
            pg.setStatus(cartao ? StatusPagamento.APROVADO : StatusPagamento.PENDENTE);
            pedido.adicionarPagamento(pg);
        }
        boolean tudoAprovado = pedido.getPagamentos().stream().allMatch(p -> p.getStatus() == StatusPagamento.APROVADO);
        pedido.setStatus(tudoAprovado ? StatusPedido.PAGO : StatusPedido.AGUARDANDO_PAGAMENTO);

        pedidos.save(pedido);

        carrinho.getItens().clear();
        carrinho.setCupom(null);
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<Pedido> doCliente(Long clienteId) {
        return pedidos.findByClienteIdOrderByCriadoEmDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> daLoja(Long lojaId) {
        return pedidos.buscarPorLoja(lojaId);
    }

    /** Ações da loja sobre o andamento do pedido. */
    @Transactional
    public void atualizarStatus(Long lojaId, Long pedidoId, String acao) {
        Pedido p = pedidos.findById(pedidoId).filter(x -> x.contemLoja(lojaId))
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));
        switch (acao) {
            case "confirmar-pagamento" -> {
                if (p.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) throw new RegraNegocioException("O pedido não está aguardando pagamento.");
                p.getPagamentos().stream().filter(pg -> pg.getStatus() == StatusPagamento.PENDENTE)
                        .forEach(pg -> pg.setStatus(StatusPagamento.APROVADO));
                p.setStatus(StatusPedido.PAGO);
            }
            case "enviar" -> {
                if (p.getStatus() != StatusPedido.PAGO) throw new RegraNegocioException("Só é possível enviar pedidos pagos.");
                p.setStatus(StatusPedido.ENVIADO);
            }
            case "entregar" -> {
                if (p.getStatus() != StatusPedido.ENVIADO) throw new RegraNegocioException("Só é possível entregar pedidos enviados.");
                p.setStatus(StatusPedido.ENTREGUE);
            }
            default -> throw new RegraNegocioException("Ação inválida.");
        }
    }
}
