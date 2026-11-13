package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.Avaliacao;
import br.ufscar.multiloja.domain.ItemPedido;
import br.ufscar.multiloja.form.AvaliacaoForm;
import br.ufscar.multiloja.repository.AvaliacaoRepository;
import br.ufscar.multiloja.repository.ItemPedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF012 / RN004: avaliação sempre ligada a um item de pedido do próprio cliente. */
@Service
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacoes;
    private final ItemPedidoRepository itens;

    public AvaliacaoService(AvaliacaoRepository avaliacoes, ItemPedidoRepository itens) {
        this.avaliacoes = avaliacoes;
        this.itens = itens;
    }

    @Transactional(readOnly = true)
    public ItemPedido itemDoCliente(Long clienteId, Long itemPedidoId) {
        return itens.findByIdAndPedidoClienteId(itemPedidoId, clienteId)
                .orElseThrow(() -> new RegraNegocioException("Item de pedido não encontrado."));
    }

    @Transactional
    public Avaliacao avaliar(Long clienteId, AvaliacaoForm f) {
        ItemPedido item = itemDoCliente(clienteId, f.getItemPedidoId());
        if (avaliacoes.existsByItemPedidoId(item.getId())) throw new RegraNegocioException("Este item já foi avaliado.");
        Avaliacao a = new Avaliacao();
        a.setItemPedido(item);
        a.setNota(f.getNota());
        a.setComentario(f.getComentario() == null || f.getComentario().isBlank() ? null : f.getComentario().trim());
        return avaliacoes.save(a);
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> doProduto(Long produtoId) {
        return avaliacoes.findByItemPedidoVariacaoProdutoIdOrderByCriadoEmDesc(produtoId);
    }

    @Transactional(readOnly = true)
    public Double mediaDoProduto(Long produtoId) {
        return avaliacoes.mediaPorProduto(produtoId);
    }
}
