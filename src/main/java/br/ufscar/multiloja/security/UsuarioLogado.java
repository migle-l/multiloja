package br.ufscar.multiloja.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Usuário autenticado: pode ser CLIENTE ou LOJA (o id é da tabela correspondente). */
public class UsuarioLogado extends User {
    private final Long id;
    private final String nome;
    private final String tipo;

    public UsuarioLogado(String email, String senhaHash, Long id, String nome, String tipo) {
        super(email, senhaHash, List.of(new SimpleGrantedAuthority("ROLE_" + tipo)));
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
}
