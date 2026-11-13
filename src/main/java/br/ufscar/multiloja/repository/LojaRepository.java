package br.ufscar.multiloja.repository;

import br.ufscar.multiloja.domain.Loja;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojaRepository extends JpaRepository<Loja, Long> {
    Optional<Loja> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);
}
