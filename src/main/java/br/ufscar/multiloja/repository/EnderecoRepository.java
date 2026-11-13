package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Endereco;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByClienteIdAndAtivoTrue(Long clienteId);
    Optional<Endereco> findByIdAndClienteId(Long id, Long clienteId);
}
