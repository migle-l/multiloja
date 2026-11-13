package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.*;
import br.ufscar.multiloja.form.CategoriaForm;
import br.ufscar.multiloja.form.ProdutoForm;
import br.ufscar.multiloja.form.VariacaoForm;
import br.ufscar.multiloja.repository.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo público (busca) e gestão de categorias, produtos e variações pela loja (RF003–RF005). */
@Service
public class CatalogoService {
    private static final int POR_PAGINA = 12; // RNF003: listagem paginada mantém a consulta rápida

    private final ProdutoRepository produtos;
    private final CategoriaRepository categorias;
    private final VariacaoProdutoRepository variacoes;
    private final LojaRepository lojas;

    public CatalogoService(ProdutoRepository produtos, CategoriaRepository categorias,
                           VariacaoProdutoRepository variacoes, LojaRepository lojas) {
        this.produtos = produtos;
        this.categorias = categorias;
        this.variacoes = variacoes;
        this.lojas = lojas;
    }

    // ---------- consulta pública ----------

    @Transactional(readOnly = true)
    public Page<Produto> buscar(String q, Long lojaId, int pagina) {
        String termo = q == null ? "" : q.trim();
        PageRequest page = PageRequest.of(Math.max(pagina, 0), POR_PAGINA, Sort.by("criadoEm").descending());
        return lojaId == null ? produtos.buscar(termo, page) : produtos.buscarPorLoja(termo, lojaId, page);
    }

    @Transactional(readOnly = true)
    public Produto obterProduto(Long id) {
        return produtos.findById(id).filter(Produto::isAtivo)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Loja> listarLojas() {
        return lojas.findAll(Sort.by("nome"));
    }

    // ---------- categorias (RF003) ----------

    @Transactional(readOnly = true)
    public List<Categoria> categoriasDaLoja(Long lojaId) {
        return categorias.findByLojaIdOrderByNomeAsc(lojaId);
    }

    @Transactional
    public Categoria criarCategoria(Long lojaId, CategoriaForm f) {
        String nome = f.getNome().trim();
        if (categorias.existsByLojaIdAndNomeIgnoreCase(lojaId, nome)) {
            throw new RegraNegocioException("Você já tem uma categoria com este nome.");
        }
        Categoria c = new Categoria();
        c.setLoja(lojas.getReferenceById(lojaId));
        c.setNome(nome);
        c.setDescricao(f.getDescricao());
        return categorias.save(c);
    }

    // ---------- produtos (RF004) ----------

    @Transactional(readOnly = true)
    public List<Produto> produtosDaLoja(Long lojaId) {
        return produtos.findByLojaIdOrderByNomeAsc(lojaId);
    }

    @Transactional(readOnly = true)
    public Produto produtoDaLoja(Long lojaId, Long produtoId) {
        return produtos.findByIdAndLojaId(produtoId, lojaId)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
    }

    @Transactional
    public Produto criarProduto(Long lojaId, ProdutoForm f) {
        String codigo = f.getCodigo().trim();
        if (produtos.existsByLojaIdAndCodigo(lojaId, codigo)) {
            throw new RegraNegocioException("Já existe um produto com este código na sua loja.");
        }
        Produto p = new Produto();
        p.setLoja(lojas.getReferenceById(lojaId));
        p.setCodigo(codigo);
        preencher(p, lojaId, f);
        return produtos.save(p);
    }

    @Transactional
    public Produto atualizarProduto(Long lojaId, Long produtoId, ProdutoForm f) {
        Produto p = produtoDaLoja(lojaId, produtoId);
        String codigo = f.getCodigo().trim();
        if (!codigo.equals(p.getCodigo()) && produtos.existsByLojaIdAndCodigo(lojaId, codigo)) {
            throw new RegraNegocioException("Já existe um produto com este código na sua loja.");
        }
        p.setCodigo(codigo);
        preencher(p, lojaId, f);
        return p;
    }

    private void preencher(Produto p, Long lojaId, ProdutoForm f) {
        Categoria categoria = categorias.findByIdAndLojaId(f.getCategoriaId(), lojaId)
                .orElseThrow(() -> new RegraNegocioException("Categoria inválida."));
        p.setCategoria(categoria);
        p.setNome(f.getNome().trim());
        p.setDescricao(f.getDescricao());
        p.setPrecoBase(f.getPrecoBase());
        p.setEstoque(f.getEstoque());
        p.setAtivo(f.isAtivo());
    }

    // ---------- variações (RF005) ----------

    @Transactional
    public VariacaoProduto adicionarVariacao(Long lojaId, Long produtoId, VariacaoForm f) {
        Produto p = produtoDaLoja(lojaId, produtoId);
        boolean semCor = f.getCor() == null || f.getCor().isBlank();
        boolean semTamanho = f.getTamanho() == null || f.getTamanho().isBlank();
        if (semCor && semTamanho) throw new RegraNegocioException("Informe ao menos a cor ou o tamanho.");

        VariacaoProduto v = new VariacaoProduto();
        v.setProduto(p);
        v.setCor(semCor ? null : f.getCor().trim());
        v.setTamanho(semTamanho ? null : f.getTamanho().trim());
        v.setPrecoAdicional(f.getPrecoAdicional());
        v.setEstoque(f.getEstoque());
        p.getVariacoes().add(v);
        return variacoes.save(v);
    }

    /** Atualiza preço adicional e estoque; devolve o id do produto para redirecionar a tela. */
    @Transactional
    public Long atualizarVariacao(Long lojaId, Long variacaoId, BigDecimal precoAdicional, int estoque) {
        VariacaoProduto v = variacoes.findByIdAndProdutoLojaId(variacaoId, lojaId)
                .orElseThrow(() -> new RegraNegocioException("Variação não encontrada."));
        if (precoAdicional == null || precoAdicional.signum() < 0) {
            throw new RegraNegocioException("O preço adicional não pode ser negativo.");
        }
        if (estoque < 0) throw new RegraNegocioException("O estoque não pode ser negativo.");
        v.setPrecoAdicional(precoAdicional);
        v.setEstoque(estoque);
        return v.getProduto().getId();
    }
}
