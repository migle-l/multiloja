package br.ufscar.multiloja.form;

import br.ufscar.multiloja.validation.Cpf;
import jakarta.validation.constraints.*;

public class ClienteForm {
    @NotBlank(message = "Informe o CPF") @Cpf
    private String cpf;
    @NotBlank(message = "Informe o nome completo") @Size(min = 3, message = "Nome muito curto")
    private String nome;
    @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido")
    private String email;
    @NotBlank(message = "Informe o telefone")
    private String telefone;
    @NotBlank(message = "Informe a senha") @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
    private String senha;
    @NotBlank(message = "Confirme a senha")
    private String confirmacaoSenha;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}
