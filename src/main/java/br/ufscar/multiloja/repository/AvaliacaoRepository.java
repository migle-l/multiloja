package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Avaliacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    boolean existsByItemPedidoId(Long itemPedidoId);
    List<Avaliacao> findByItemPedidoVariacaoProdutoIdOrderByCriadoEmDesc(Long produtoId);

    @Query("select avg(a.nota) from Avaliacao a where a.itemPedido.variacao.produto.id = :produtoId")
    Double mediaPorProduto(@Param("produtoId") Long produtoId);
}
