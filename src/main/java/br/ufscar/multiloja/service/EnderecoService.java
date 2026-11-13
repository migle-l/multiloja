package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.Endereco;
import br.ufscar.multiloja.form.EnderecoForm;
import br.ufscar.multiloja.repository.ClienteRepository;
import br.ufscar.multiloja.repository.EnderecoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF002: múltiplos endereços por cliente. */
@Service
public class EnderecoService {
    private final EnderecoRepository enderecos;
    private final ClienteRepository clientes;

    public EnderecoService(EnderecoRepository enderecos, ClienteRepository clientes) {
        this.enderecos = enderecos;
        this.clientes = clientes;
    }

    @Transactional(readOnly = true)
    public List<Endereco> listar(Long clienteId) {
        return enderecos.findByClienteIdAndAtivoTrue(clienteId);
    }

    @Transactional
    public Endereco criar(Long clienteId, EnderecoForm f) {
        Endereco e = new Endereco();
        e.setCliente(clientes.getReferenceById(clienteId));
        e.setLogradouro(f.getLogradouro().trim());
        e.setComplemento(f.getComplemento());
        e.setBairro(f.getBairro().trim());
        e.setCidade(f.getCidade().trim());
        e.setEstado(f.getEstado().trim().toUpperCase());
        e.setCep(f.getCep().trim());
        e.setTipo(f.getTipo());
        return enderecos.save(e);
    }

    @Transactional
    public void remover(Long clienteId, Long enderecoId) {
        Endereco e = enderecos.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() -> new RegraNegocioException("Endereço não encontrado."));
        e.setAtivo(false);
    }
}
