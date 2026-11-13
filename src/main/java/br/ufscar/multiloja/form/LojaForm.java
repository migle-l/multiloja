package br.ufscar.multiloja.form;

import br.ufscar.multiloja.validation.Cnpj;
import jakarta.validation.constraints.*;

public class LojaForm {
    @NotBlank(message = "Informe o CNPJ") @Cnpj
    private String cnpj;
    @NotBlank(message = "Informe o nome da loja")
    private String nome;
    @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido")
    private String email;
    @NotBlank(message = "Informe o telefone")
    private String telefone;
    @NotBlank(message = "Informe a senha") @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
    private String senha;
    @NotBlank(message = "Confirme a senha")
    private String confirmacaoSenha;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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
