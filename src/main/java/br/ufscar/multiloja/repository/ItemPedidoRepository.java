package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.ItemPedido;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    Optional<ItemPedido> findByIdAndPedidoClienteId(Long id, Long clienteId);
}
