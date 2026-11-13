package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.form.ClienteForm;
import br.ufscar.multiloja.form.LojaForm;
import br.ufscar.multiloja.service.CadastroService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Login (tratado pelo Spring Security) e cadastros de cliente e loja. */
@Controller
public class AuthController {
    private final CadastroService cadastro;

    public AuthController(CadastroService cadastro) {
        this.cadastro = cadastro;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro/cliente")
    public String formCliente(Model model) {
        model.addAttribute("form", new ClienteForm());
        return "cadastro-cliente";
    }

    @PostMapping("/cadastro/cliente")
    public String cadastrarCliente(@Valid @ModelAttribute("form") ClienteForm form, BindingResult br,
                                   Model model, RedirectAttributes ra) {
        if (form.getSenha() != null && !form.getSenha().equals(form.getConfirmacaoSenha())) {
            br.rejectValue("confirmacaoSenha", "senha.diferente", "As senhas não conferem");
        }
        if (br.hasErrors()) return "cadastro-cliente";
        try {
            cadastro.cadastrarCliente(form);
            ra.addFlashAttribute("sucesso", "Conta criada! Entre com seu e-mail e senha.");
            return "redirect:/login";
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro-cliente";
        }
    }

    @GetMapping("/cadastro/loja")
    public String formLoja(Model model) {
        model.addAttribute("form", new LojaForm());
        return "cadastro-loja";
    }

    @PostMapping("/cadastro/loja")
    public String cadastrarLoja(@Valid @ModelAttribute("form") LojaForm form, BindingResult br,
                                Model model, RedirectAttributes ra) {
        if (form.getSenha() != null && !form.getSenha().equals(form.getConfirmacaoSenha())) {
            br.rejectValue("confirmacaoSenha", "senha.diferente", "As senhas não conferem");
        }
        if (br.hasErrors()) return "cadastro-loja";
        try {
            cadastro.cadastrarLoja(form);
            ra.addFlashAttribute("sucesso", "Loja cadastrada! Entre para começar a vender.");
            return "redirect:/login";
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro-loja";
        }
    }
}
