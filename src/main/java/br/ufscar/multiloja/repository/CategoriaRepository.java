package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByLojaIdOrderByNomeAsc(Long lojaId);
    Optional<Categoria> findByIdAndLojaId(Long id, Long lojaId);
    boolean existsByLojaIdAndNomeIgnoreCase(Long lojaId, String nome);
}
