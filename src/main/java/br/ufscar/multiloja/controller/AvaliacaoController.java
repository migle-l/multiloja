package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.domain.ItemPedido;
import br.ufscar.multiloja.form.AvaliacaoForm;
import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.AvaliacaoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** RF012: o cliente avalia itens que comprou. */
@Controller
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    private final AvaliacaoService avaliacoes;

    public AvaliacaoController(AvaliacaoService avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    @GetMapping("/nova")
    public String nova(@AuthenticationPrincipal UsuarioLogado usuario, @RequestParam Long itemId,
                       Model model, RedirectAttributes ra) {
        try {
            ItemPedido item = avaliacoes.itemDoCliente(usuario.getId(), itemId);
            if (item.getAvaliacao() != null) throw new RegraNegocioException("Este item já foi avaliado.");
            AvaliacaoForm form = new AvaliacaoForm();
            form.setItemPedidoId(itemId);
            form.setNota(5);
            model.addAttribute("form", form);
            model.addAttribute("item", item);
            return "avaliar";
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @PostMapping
    public String salvar(@AuthenticationPrincipal UsuarioLogado usuario,
                         @Valid @ModelAttribute("form") AvaliacaoForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        try {
            ItemPedido item = avaliacoes.itemDoCliente(usuario.getId(), form.getItemPedidoId());
            if (!br.hasErrors()) {
                avaliacoes.avaliar(usuario.getId(), form);
                ra.addFlashAttribute("sucesso", "Avaliação registrada. Obrigado!");
                return "redirect:/pedidos";
            }
            model.addAttribute("item", item);
            return "avaliar";
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos";
        }
    }
}
