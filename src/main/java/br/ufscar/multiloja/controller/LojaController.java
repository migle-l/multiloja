package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.form.CategoriaForm;
import br.ufscar.multiloja.form.CupomForm;
import br.ufscar.multiloja.domain.TipoCupom;
import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.CatalogoService;
import br.ufscar.multiloja.service.CupomService;
import br.ufscar.multiloja.service.PedidoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Painel da loja: resumo, categorias, cupons e pedidos. */
@Controller
@RequestMapping("/loja")
public class LojaController {
    private final CatalogoService catalogo;
    private final CupomService cupons;
    private final PedidoService pedidos;

    public LojaController(CatalogoService catalogo, CupomService cupons, PedidoService pedidos) {
        this.catalogo = catalogo;
        this.cupons = cupons;
        this.pedidos = pedidos;
    }

    @GetMapping
    public String painel(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        Long id = usuario.getId();
        model.addAttribute("totalProdutos", catalogo.produtosDaLoja(id).size());
        model.addAttribute("totalCategorias", catalogo.categoriasDaLoja(id).size());
        model.addAttribute("totalCupons", cupons.listar(id).size());
        model.addAttribute("totalPedidos", pedidos.daLoja(id).size());
        return "loja/painel";
    }

    // ---------- categorias (RF003) ----------

    @GetMapping("/categorias")
    public String categorias(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", new CategoriaForm());
        model.addAttribute("categorias", catalogo.categoriasDaLoja(usuario.getId()));
        return "loja/categorias";
    }

    @PostMapping("/categorias")
    public String criarCategoria(@AuthenticationPrincipal UsuarioLogado usuario,
                                 @Valid @ModelAttribute("form") CategoriaForm form, BindingResult br,
                                 Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                catalogo.criarCategoria(usuario.getId(), form);
                ra.addFlashAttribute("sucesso", "Categoria criada.");
                return "redirect:/loja/categorias";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        model.addAttribute("categorias", catalogo.categoriasDaLoja(usuario.getId()));
        return "loja/categorias";
    }

    // ---------- cupons (RN005) ----------

    @GetMapping("/cupons")
    public String cupons(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", new CupomForm());
        popularCupons(usuario, model);
        return "loja/cupons";
    }

    @PostMapping("/cupons")
    public String criarCupom(@AuthenticationPrincipal UsuarioLogado usuario,
                             @Valid @ModelAttribute("form") CupomForm form, BindingResult br,
                             Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                cupons.criar(usuario.getId(), form);
                ra.addFlashAttribute("sucesso", "Cupom criado.");
                return "redirect:/loja/cupons";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        popularCupons(usuario, model);
        return "loja/cupons";
    }

    @PostMapping("/cupons/{id}/desativar")
    public String desativarCupom(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, RedirectAttributes ra) {
        try {
            cupons.desativar(usuario.getId(), id);
            ra.addFlashAttribute("sucesso", "Cupom desativado.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/loja/cupons";
    }

    private void popularCupons(UsuarioLogado usuario, Model model) {
        model.addAttribute("cupons", cupons.listar(usuario.getId()));
        model.addAttribute("categorias", catalogo.categoriasDaLoja(usuario.getId()));
        model.addAttribute("tipos", TipoCupom.values());
    }

    // ---------- pedidos ----------

    @GetMapping("/pedidos")
    public String pedidos(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("pedidos", pedidos.daLoja(usuario.getId()));
        model.addAttribute("lojaId", usuario.getId());
        return "loja/pedidos";
    }

    @PostMapping("/pedidos/{id}/{acao}")
    public String atualizarPedido(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                                  @PathVariable String acao, RedirectAttributes ra) {
        try {
            pedidos.atualizarStatus(usuario.getId(), id, acao);
            ra.addFlashAttribute("sucesso", "Pedido atualizado.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/loja/pedidos";
    }
}
