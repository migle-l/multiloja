package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Carrinho;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByClienteId(Long clienteId);
}
