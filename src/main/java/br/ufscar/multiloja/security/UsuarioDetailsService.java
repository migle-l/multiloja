package br.ufscar.multiloja.security;

import br.ufscar.multiloja.domain.Cliente;
import br.ufscar.multiloja.domain.Loja;
import br.ufscar.multiloja.repository.ClienteRepository;
import br.ufscar.multiloja.repository.LojaRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** RF001: login por e-mail e senha para clientes e lojas. */
@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final ClienteRepository clientes;
    private final LojaRepository lojas;

    public UsuarioDetailsService(ClienteRepository clientes, LojaRepository lojas) {
        this.clientes = clientes;
        this.lojas = lojas;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String e = email == null ? "" : email.trim().toLowerCase();

        Optional<Cliente> cliente = clientes.findByEmail(e);
        if (cliente.isPresent()) {
            Cliente c = cliente.get();
            return new UsuarioLogado(c.getEmail(), c.getSenhaHash(), c.getId(), c.getNome(), "CLIENTE");
        }
        Optional<Loja> loja = lojas.findByEmail(e);
        if (loja.isPresent()) {
            Loja l = loja.get();
            return new UsuarioLogado(l.getEmail(), l.getSenhaHash(), l.getId(), l.getNome(), "LOJA");
        }
        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}
