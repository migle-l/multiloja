package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Cupom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupomRepository extends JpaRepository<Cupom, Long> {
    List<Cupom> findByLojaIdOrderByCodigoAsc(Long lojaId);
    Optional<Cupom> findByCodigoIgnoreCase(String codigo);
    Optional<Cupom> findByIdAndLojaId(Long id, Long lojaId);
    boolean existsByCodigoIgnoreCase(String codigo);
}
