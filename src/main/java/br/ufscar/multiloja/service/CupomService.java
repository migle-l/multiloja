package br.ufscar.multiloja.service;

import br.ufscar.multiloja.domain.Categoria;
import br.ufscar.multiloja.domain.Cupom;
import br.ufscar.multiloja.domain.TipoCupom;
import br.ufscar.multiloja.form.CupomForm;
import br.ufscar.multiloja.repository.CategoriaRepository;
import br.ufscar.multiloja.repository.CupomRepository;
import br.ufscar.multiloja.repository.LojaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cupons de desconto aplicáveis a categorias (RN005). */
@Service
public class CupomService {
    private final CupomRepository cupons;
    private final CategoriaRepository categorias;
    private final LojaRepository lojas;

    public CupomService(CupomRepository cupons, CategoriaRepository categorias, LojaRepository lojas) {
        this.cupons = cupons;
        this.categorias = categorias;
        this.lojas = lojas;
    }

    @Transactional(readOnly = true)
    public List<Cupom> listar(Long lojaId) {
        return cupons.findByLojaIdOrderByCodigoAsc(lojaId);
    }

    @Transactional
    public Cupom criar(Long lojaId, CupomForm f) {
        String codigo = f.getCodigo().trim().toUpperCase();
        if (cupons.existsByCodigoIgnoreCase(codigo)) throw new RegraNegocioException("Este código de cupom já existe.");
        if (f.getTipo() == TipoCupom.PERCENTUAL && f.getValor().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RegraNegocioException("O desconto percentual não pode passar de 100%.");
        }
        Categoria categoria = categorias.findByIdAndLojaId(f.getCategoriaId(), lojaId)
                .orElseThrow(() -> new RegraNegocioException("Categoria inválida."));

        Cupom c = new Cupom();
        c.setLoja(lojas.getReferenceById(lojaId));
        c.setCategoria(categoria);
        c.setCodigo(codigo);
        c.setTipo(f.getTipo());
        c.setValor(f.getValor());
        c.setValidade(f.getValidade());
        return cupons.save(c);
    }

    @Transactional
    public void desativar(Long lojaId, Long cupomId) {
        Cupom c = cupons.findByIdAndLojaId(cupomId, lojaId)
                .orElseThrow(() -> new RegraNegocioException("Cupom não encontrado."));
        c.setAtivo(false);
    }
}
