package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.VariacaoProduto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariacaoProdutoRepository extends JpaRepository<VariacaoProduto, Long> {
    Optional<VariacaoProduto> findByIdAndProdutoLojaId(Long id, Long lojaId);
}
