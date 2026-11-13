package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.domain.TipoEndereco;
import br.ufscar.multiloja.form.EnderecoForm;
import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.EnderecoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enderecos")
public class EnderecoController {
    private final EnderecoService enderecos;

    public EnderecoController(EnderecoService enderecos) {
        this.enderecos = enderecos;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", new EnderecoForm());
        popular(usuario, model);
        return "enderecos";
    }

    @PostMapping
    public String criar(@AuthenticationPrincipal UsuarioLogado usuario,
                        @Valid @ModelAttribute("form") EnderecoForm form, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            popular(usuario, model);
            return "enderecos";
        }
        enderecos.criar(usuario.getId(), form);
        ra.addFlashAttribute("sucesso", "Endereço cadastrado.");
        return "redirect:/enderecos";
    }

    @PostMapping("/{id}/remover")
    public String remover(@AuthenticationPrincipal UsuarioLogado usuario, @PathVariable Long id, RedirectAttributes ra) {
        try {
            enderecos.remover(usuario.getId(), id);
            ra.addFlashAttribute("sucesso", "Endereço removido.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/enderecos";
    }

    private void popular(UsuarioLogado usuario, Model model) {
        model.addAttribute("enderecos", enderecos.listar(usuario.getId()));
        model.addAttribute("tipos", TipoEndereco.values());
    }
}
