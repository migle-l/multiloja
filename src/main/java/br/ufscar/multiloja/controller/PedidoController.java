package br.ufscar.multiloja.controller;

import br.ufscar.multiloja.domain.Carrinho;
import br.ufscar.multiloja.domain.Endereco;
import br.ufscar.multiloja.domain.FormaPagamento;
import br.ufscar.multiloja.domain.Pedido;
import br.ufscar.multiloja.form.CheckoutForm;
import br.ufscar.multiloja.security.UsuarioLogado;
import br.ufscar.multiloja.service.CarrinhoService;
import br.ufscar.multiloja.service.EnderecoService;
import br.ufscar.multiloja.service.PedidoService;
import br.ufscar.multiloja.service.RegraNegocioException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Checkout (RF009–RF011) e histórico de pedidos do cliente. */
@Controller
public class PedidoController {
    private final PedidoService pedidos;
    private final CarrinhoService carrinhos;
    private final EnderecoService enderecos;

    public PedidoController(PedidoService pedidos, CarrinhoService carrinhos, EnderecoService enderecos) {
        this.pedidos = pedidos;
        this.carrinhos = carrinhos;
        this.enderecos = enderecos;
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UsuarioLogado usuario, Model model, RedirectAttributes ra) {
        Carrinho carrinho = carrinhos.obter(usuario.getId());
        if (carrinho.isVazio()) {
            ra.addFlashAttribute("erro", "Seu carrinho está vazio.");
            return "redirect:/carrinho";
        }
        List<Endereco> lista = enderecos.listar(usuario.getId());
        if (lista.isEmpty()) {
            ra.addFlashAttribute("erro", "Cadastre um endereço de entrega para continuar.");
            return "redirect:/enderecos";
        }
        CheckoutForm form = new CheckoutForm();
        form.setEnderecoId(lista.get(0).getId());
        form.getPagamentos().get(0).setForma(FormaPagamento.CARTAO_CREDITO);
        form.getPagamentos().get(0).setValor(carrinho.getTotal());
        model.addAttribute("checkout", form);
        popular(usuario, model, carrinho, lista);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String finalizar(@AuthenticationPrincipal UsuarioLogado usuario,
                            @Valid @ModelAttribute("checkout") CheckoutForm form, BindingResult br,
                            Model model, RedirectAttributes ra) {
        if (!br.hasErrors()) {
            try {
                Pedido pedido = pedidos.finalizar(usuario.getId(), form);
                ra.addFlashAttribute("sucesso", "Pedido " + pedido.getNumero() + " realizado com sucesso!");
                return "redirect:/pedidos";
            } catch (RegraNegocioException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        popular(usuario, model, carrinhos.obter(usuario.getId()), enderecos.listar(usuario.getId()));
        return "checkout";
    }

    @GetMapping("/pedidos")
    public String listar(@AuthenticationPrincipal UsuarioLogado usuario, Model model) {
        model.addAttribute("pedidos", pedidos.doCliente(usuario.getId()));
        return "pedidos";
    }

    private void popular(UsuarioLogado usuario, Model model, Carrinho carrinho, List<Endereco> lista) {
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("enderecos", lista);
        model.addAttribute("formas", FormaPagamento.values());
    }
}
