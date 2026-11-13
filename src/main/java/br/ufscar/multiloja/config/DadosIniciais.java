package br.ufscar.multiloja.config;

import br.ufscar.multiloja.domain.*;
import br.ufscar.multiloja.form.*;
import br.ufscar.multiloja.repository.LojaRepository;
import br.ufscar.multiloja.service.*;
import java.math.BigDecimal;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Cria dados de demonstração na primeira execução (banco vazio). */
@Component
public class DadosIniciais implements ApplicationRunner {
    private final LojaRepository lojas;
    private final CadastroService cadastro;
    private final CatalogoService catalogo;
    private final CupomService cupons;
    private final EnderecoService enderecos;

    public DadosIniciais(LojaRepository lojas, CadastroService cadastro, CatalogoService catalogo,
                         CupomService cupons, EnderecoService enderecos) {
        this.lojas = lojas;
        this.cadastro = cadastro;
        this.catalogo = catalogo;
        this.cupons = cupons;
        this.enderecos = enderecos;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (lojas.count() > 0) return;

        ClienteForm cf = new ClienteForm();
        cf.setCpf("529.982.247-25");
        cf.setNome("Cliente Teste");
        cf.setEmail("cliente@exemplo.com");
        cf.setTelefone("(16) 98888-0000");
        cf.setSenha("senha12345");
        Cliente cliente = cadastro.cadastrarCliente(cf);

        EnderecoForm ef = new EnderecoForm();
        ef.setLogradouro("Rua das Flores, 100");
        ef.setBairro("Centro");
        ef.setCidade("São Carlos");
        ef.setEstado("SP");
        ef.setCep("13560-000");
        enderecos.criar(cliente.getId(), ef);

        LojaForm lf = new LojaForm();
        lf.setCnpj("11.222.333/0001-81");
        lf.setNome("Casa do Algodão");
        lf.setEmail("loja@exemplo.com");
        lf.setTelefone("(16) 99999-0000");
        lf.setSenha("senha12345");
        Loja loja = cadastro.cadastrarLoja(lf);

        CategoriaForm cat = new CategoriaForm();
        cat.setNome("Camisetas");
        cat.setDescricao("Camisetas básicas e estampadas");
        Categoria camisetas = catalogo.criarCategoria(loja.getId(), cat);

        produto(loja, camisetas, "CAM-001", "Camiseta Básica Algodão", "100% algodão, corte reto.", "59.90",
                new String[][]{{"Azul", "M", "0", "20"}, {"Azul", "G", "5", "15"}, {"Preta", "M", "0", "30"}});
        produto(loja, camisetas, "CAM-002", "Camiseta Estampada Retrô", "Estampa em silk, malha penteada.", "79.90",
                new String[][]{{"Branca", "P", "0", "10"}, {"Branca", "M", "0", "10"}});

        CupomForm cu = new CupomForm();
        cu.setCodigo("CAMISA10");
        cu.setCategoriaId(camisetas.getId());
        cu.setTipo(TipoCupom.PERCENTUAL);
        cu.setValor(BigDecimal.TEN);
        cupons.criar(loja.getId(), cu);

        System.out.println("Dados de exemplo criados. Logins (senha12345): loja@exemplo.com (LOJA) e cliente@exemplo.com (CLIENTE). Cupom: CAMISA10");
    }

    private void produto(Loja loja, Categoria cat, String codigo, String nome, String descricao, String preco, String[][] variacoes) {
        ProdutoForm pf = new ProdutoForm();
        pf.setCodigo(codigo);
        pf.setNome(nome);
        pf.setDescricao(descricao);
        pf.setPrecoBase(new BigDecimal(preco));
        pf.setEstoque(100);
        pf.setCategoriaId(cat.getId());
        Produto p = catalogo.criarProduto(loja.getId(), pf);
        for (String[] v : variacoes) {
            VariacaoForm vf = new VariacaoForm();
            vf.setCor(v[0]);
            vf.setTamanho(v[1]);
            vf.setPrecoAdicional(new BigDecimal(v[2]));
            vf.setEstoque(Integer.parseInt(v[3]));
            catalogo.adicionarVariacao(loja.getId(), p.getId(), vf);
        }
    }
}
