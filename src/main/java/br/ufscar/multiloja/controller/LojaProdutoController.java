package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.domain.Produto;
import br.ufscar.multiloja.form.ProdutoForm;
import br.ufscar.multiloja.form.VariacaoForm;
import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.CatalogoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestão de produtos e variações pela loja (RF004, RF005). */
@Controller
@RequestMapping("/loja")
public class LojaProdutoController {
    private final CatalogoService catalogo;

    public LojaProdutoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/produtos")
    public String lista(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("produtos", catalogo.produtosDaLoja(usuario.getId()));
        return "loja/produtos";
    }

    @GetMapping("/produtos/novo")
    public String novo(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("form", new ProdutoForm());
        prepararTela(model, usuario.getId(), null);
        return "loja/produto-form";
    }

    @PostMapping("/produtos")
    public String criar(@AuthenticationPrincipal UsuarioLogado usuario,
                        @Valid @ModelAttribute("form") ProdutoForm form, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                Produto p = catalogo.criarProduto(usuario.getId(), form);
                ra.addFlashAttribute("sucesso", "Produto criado. Agora cadastre as variações (cor/tamanho) e o estoque de cada uma.");
                return "redirect:/loja/produtos/" + p.getId();
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        prepararTela(model, usuario.getId(), null);
        return "loja/produto-form";
    }

    @GetMapping("/produtos/{id}")
    public String editar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                         Model model, RedirectAttributes ra) {
        try {
            Produto p = catalogo.produtoDaLoja(usuario.getId(), id);
            model.addAttribute("form", ProdutoForm.de(p));
            prepararTela(model, usuario.getId(), id);
            return "loja/produto-form";
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/loja/produtos";
        }
    }

    @PostMapping("/produtos/{id}")
    public String atualizar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                            @Valid @ModelAttribute("form") ProdutoForm form, BindingResult br,
                            Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                catalogo.atualizarProduto(usuario.getId(), id, form);
                ra.addFlashAttribute("sucesso", "Produto atualizado.");
                return "redirect:/loja/produtos/" + id;
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        prepararTela(model, usuario.getId(), id);
        return "loja/produto-form";
    }

    @PostMapping("/produtos/{id}/variacoes")
    public String adicionarVariacao(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                    @Valid @ModelAttribute("variacaoForm") VariacaoForm variacaoForm, BindingResult br,
                                    Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                catalogo.adicionarVariacao(usuario.getId(), id, variacaoForm);
                ra.addFlashAttribute("sucesso", "Variação adicionada.");
                return "redirect:/loja/produtos/" + id;
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", ProdutoForm.de(catalogo.produtoDaLoja(usuario.getId(), id)));
        }
        prepararTela(model, usuario.getId(), id);
        return "loja/produto-form";
    }

    @PostMapping("/variacoes/{id}")
    public String atualizarVariacao(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                    @RequestParam BigDecimal precoAdicional, @RequestParam int estoque,
                                    RedirectAttributes ra) {
        try {
            Long produtoId = catalogo.atualizarVariacao(usuario.getId(), id, precoAdicional, estoque);
            ra.addFlashAttribute("sucesso", "Variação atualizada.");
            return "redirect:/loja/produtos/" + produtoId;
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/loja/produtos";
        }
    }

    private void prepararTela(Model model, Long lojaId, Long produtoId) {
        model.addAttribute("categorias", catalogo.categoriasDaLoja(lojaId));
        model.addAttribute("produtoId", produtoId);
        if (produtoId != null) {
            model.addAttribute("produto", catalogo.produtoDaLoja(lojaId, produtoId));
            if (!model.containsAttribute("variacaoForm")) model.addAttribute("variacaoForm", new VariacaoForm());
        }
    }
}
