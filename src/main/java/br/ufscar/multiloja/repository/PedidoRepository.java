package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    Optional<Pedido> findByIdAndClienteId(Long id, Long clienteId);

    /** Pedidos que contêm ao menos um item de produto da loja. */
    @Query("select distinct p from Pedido p join p.itens i where i.variacao.produto.loja.id = :lojaId "
            + "order by p.criadoEm desc")
    List<Pedido> buscarPorLoja(@Param("lojaId") Long lojaId);
}
