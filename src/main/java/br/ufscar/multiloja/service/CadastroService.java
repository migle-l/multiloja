package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.Carrinho;
import br.ufscar.multiloja.domain.Cliente;
import br.ufscar.multiloja.domain.Loja;
import br.ufscar.multiloja.form.ClienteForm;
import br.ufscar.multiloja.form.LojaForm;
import br.ufscar.multiloja.repository.CarrinhoRepository;
import br.ufscar.multiloja.repository.ClienteRepository;
import br.ufscar.multiloja.repository.LojaRepository;
import br.ufscar.multiloja.util.Documentos;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro de clientes (CPF) e lojas (CNPJ). */
@Service
public class CadastroService {
    private final ClienteRepository clientes;
    private final LojaRepository lojas;
    private final CarrinhoRepository carrinhos;
    private final PasswordEncoder encoder;

    public CadastroService(ClienteRepository clientes, LojaRepository lojas,
                           CarrinhoRepository carrinhos, PasswordEncoder encoder) {
        this.clientes = clientes;
        this.lojas = lojas;
        this.carrinhos = carrinhos;
        this.encoder = encoder;
    }

    @Transactional
    public Cliente cadastrarCliente(ClienteForm f) {
        String cpf = Documentos.somenteDigitos(f.getCpf());
        String email = f.getEmail().trim().toLowerCase();
        if (clientes.existsByCpf(cpf)) throw new RegraNegocioException("Já existe um cliente com este CPF.");
        validarEmailLivre(email);

        Cliente c = new Cliente();
        c.setCpf(cpf);
        c.setNome(f.getNome().trim());
        c.setEmail(email);
        c.setTelefone(f.getTelefone().trim());
        c.setSenhaHash(encoder.encode(f.getSenha())); // RNF002: senha nunca é salva em texto puro
        c = clientes.save(c);

        // RF006: cada cliente tem um carrinho único, criado junto com a conta
        Carrinho carrinho = new Carrinho();
        carrinho.setCliente(c);
        carrinhos.save(carrinho);
        return c;
    }

    @Transactional
    public Loja cadastrarLoja(LojaForm f) {
        String cnpj = Documentos.somenteDigitos(f.getCnpj());
        String email = f.getEmail().trim().toLowerCase();
        if (lojas.existsByCnpj(cnpj)) throw new RegraNegocioException("Já existe uma loja com este CNPJ.");
        validarEmailLivre(email);

        Loja l = new Loja();
        l.setCnpj(cnpj);
        l.setNome(f.getNome().trim());
        l.setEmail(email);
        l.setTelefone(f.getTelefone().trim());
        l.setSenhaHash(encoder.encode(f.getSenha()));
        return lojas.save(l);
    }

    /** O login é por e-mail para os dois perfis, então o e-mail não pode se repetir entre eles. */
    private void validarEmailLivre(String email) {
        if (clientes.existsByEmail(email) || lojas.existsByEmail(email)) {
            throw new RegraNegocioException("Este e-mail já está cadastrado.");
        }
    }
}
