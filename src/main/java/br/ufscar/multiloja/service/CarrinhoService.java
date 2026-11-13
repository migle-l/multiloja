package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.*;
import br.ufscar.multiloja.repository.CarrinhoRepository;
import br.ufscar.multiloja.repository.ClienteRepository;
import br.ufscar.multiloja.repository.CupomRepository;
import br.ufscar.multiloja.repository.VariacaoProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF006–RF008: carrinho único, itens por variação e aplicação de cupom. */
@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhos;
    private final ClienteRepository clientes;
    private final VariacaoProdutoRepository variacoes;
    private final CupomRepository cupons;

    public CarrinhoService(CarrinhoRepository carrinhos, ClienteRepository clientes,
                           VariacaoProdutoRepository variacoes, CupomRepository cupons) {
        this.carrinhos = carrinhos;
        this.clientes = clientes;
        this.variacoes = variacoes;
        this.cupons = cupons;
    }

    @Transactional
    public Carrinho obter(Long clienteId) {
        return carrinhos.findByClienteId(clienteId).orElseGet(() -> {
            Carrinho novo = new Carrinho();
            novo.setCliente(clientes.getReferenceById(clienteId));
            return carrinhos.save(novo);
        });
    }

    @Transactional
    public void adicionarItem(Long clienteId, Long variacaoId, int quantidade) {
        if (quantidade < 1) throw new RegraNegocioException("A quantidade deve ser de pelo menos 1.");
        Carrinho carrinho = obter(clienteId);
        VariacaoProduto v = variacoes.findById(variacaoId)
                .orElseThrow(() -> new RegraNegocioException("Variação não encontrada."));
        if (!v.getProduto().isAtivo()) throw new RegraNegocioException("Este produto não está mais disponível.");
        if (carrinho.quantidadeDe(v) + quantidade > v.getEstoque()) {
            throw new RegraNegocioException("Estoque insuficiente. Disponível: " + v.getEstoque() + ".");
        }
        carrinho.adicionar(v, quantidade);
    }

    /** Quantidade 0 remove o item. */
    @Transactional
    public void alterarQuantidade(Long clienteId, Long itemId, int quantidade) {
        Carrinho carrinho = obter(clienteId);
        ItemCarrinho item = carrinho.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Item não encontrado no carrinho."));
        if (quantidade < 0) throw new RegraNegocioException("Quantidade inválida.");
        if (quantidade == 0) {
            carrinho.getItens().remove(item);
            return;
        }
        if (quantidade > item.getVariacao().getEstoque()) {
            throw new RegraNegocioException("Estoque insuficiente. Disponível: " + item.getVariacao().getEstoque() + ".");
        }
        item.setQuantidade(quantidade);
    }

    @Transactional
    public void aplicarCupom(Long clienteId, String codigo) {
        Carrinho carrinho = obter(clienteId);
        Cupom cupom = cupons.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new RegraNegocioException("Cupom não encontrado."));
        if (!cupom.isUtilizavel()) throw new RegraNegocioException("Este cupom está inativo ou expirado.");
        if (carrinho.baseElegivelPara(cupom).signum() <= 0) {
            throw new RegraNegocioException(
                    "Nenhum item do carrinho pertence à categoria \"" + cupom.getCategoria().getNome() + "\", exigida por este cupom.");
        }
        carrinho.setCupom(cupom);
    }

    @Transactional
    public void removerCupom(Long clienteId) {
        obter(clienteId).setCupom(null);
    }
}
