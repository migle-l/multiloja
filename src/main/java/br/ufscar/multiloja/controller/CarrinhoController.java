package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.CarrinhoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {
    private final CarrinhoService carrinhos;

    public CarrinhoController(CarrinhoService carrinhos) {
        this.carrinhos = carrinhos;
    }

    @GetMapping
    public String ver(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("carrinho", carrinhos.obter(usuario.getId()));
        return "carrinho";
    }

    @PostMapping("/itens")
    public String adicionar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @RequestParam Long variacaoId, @RequestParam Long produtoId,
                            @RequestParam(defaultValue = "1") int quantidade, RedirectAttributes ra) {
        try {
            carrinhos.adicionarItem(usuario.getId(), variacaoId, quantidade);
            ra.addFlashAttribute("sucesso", "Item adicionado ao carrinho.");
            return "redirect:/carrinho";
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/produtos/" + produtoId;
        }
    }

    @PostMapping("/itens/{id}/quantidade")
    public String alterar(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id,
                          @RequestParam int quantidade, RedirectAttributes ra) {
        executar(ra, () -> carrinhos.alterarQuantidade(usuario.getId(), id, quantidade));
        return "redirect:/carrinho";
    }

    @PostMapping("/itens/{id}/remover")
    public String remover(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, RedirectAttributes ra) {
        executar(ra, () -> carrinhos.alterarQuantidade(usuario.getId(), id, 0));
        return "redirect:/carrinho";
    }

    @PostMapping("/cupom")
    public String aplicarCupom(@AuthenticationPrincipal UsuarioLogado usuario, @RequestParam String codigo, RedirectAttributes ra) {
        executar(ra, () -> {
            carrinhos.aplicarCupom(usuario.getId(), codigo);
            ra.addFlashAttribute("sucesso", "Cupom aplicado.");
        });
        return "redirect:/carrinho";
    }

    @PostMapping("/cupom/remover")
    public String removerCupom(@AuthenticationPrincipal UsuarioLogado usuario) {
        carrinhos.removerCupom(usuario.getId());
        return "redirect:/carrinho";
    }

    private void executar(RedirectAttributes ra, Runnable acao) {
        try {
            acao.run();
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
    }
}
