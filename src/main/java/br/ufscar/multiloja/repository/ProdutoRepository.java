package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("select p from Produto p where p.ativo = true and lower(p.nome) like lower(concat('%', :q, '%'))")
    Page<Produto> buscar(@Param("q") String q, Pageable pageable);

    @Query("select p from Produto p where p.ativo = true and p.loja.id = :lojaId "
            + "and lower(p.nome) like lower(concat('%', :q, '%'))")
    Page<Produto> buscarPorLoja(@Param("q") String q, @Param("lojaId") Long lojaId, Pageable pageable);

    List<Produto> findByLojaIdOrderByNomeAsc(Long lojaId);
    Optional<Produto> findByIdAndLojaId(Long id, Long lojaId);
    boolean existsByLojaIdAndCodigo(Long lojaId, String codigo);
}
