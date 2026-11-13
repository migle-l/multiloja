package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.domain.Produto;
import br.ufscar.multiloja.service.AvaliacaoService;
import br.ufscar.multiloja.service.CatalogoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Vitrine pública: busca de produtos e página do produto. */
@Controller
public class CatalogoController {
    private final CatalogoService catalogo;
    private final AvaliacaoService avaliacoes;

    public CatalogoController(CatalogoService catalogo, AvaliacaoService avaliacoes) {
        this.catalogo = catalogo;
        this.avaliacoes = avaliacoes;
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "") String q,
                        @RequestParam(required = false) Long lojaId,
                        @RequestParam(defaultValue = "0") int pagina,
                        Model model) {
        model.addAttribute("produtos", catalogo.buscar(q, lojaId, pagina));
        model.addAttribute("lojas", catalogo.listarLojas());
        model.addAttribute("q", q);
        model.addAttribute("lojaId", lojaId);
        return "index";
    }

    @GetMapping("/produtos/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Produto produto = catalogo.obterProduto(id);
            model.addAttribute("produto", produto);
            model.addAttribute("avaliacoes", avaliacoes.doProduto(id));
            model.addAttribute("media", avaliacoes.mediaDoProduto(id));
            return "produto";
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/";
        }
    }
}
