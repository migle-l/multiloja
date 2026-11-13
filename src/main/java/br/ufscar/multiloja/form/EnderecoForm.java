package br.ufscar.multiloja.form;

import br.ufscar.multiloja.domain.TipoEndereco;
import jakarta.validation.constraints.*;

public class EnderecoForm {
    @NotBlank(message = "Informe o logradouro")
    private String logradouro;
    private String complemento;
    @NotBlank(message = "Informe o bairro")
    private String bairro;
    @NotBlank(message = "Informe a cidade")
    private String cidade;
    @NotBlank(message = "Informe o estado") @Size(min = 2, max = 2, message = "Use a sigla do estado (ex.: SP)")
    private String estado;
    @NotBlank(message = "Informe o CEP") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String cep;
    @NotNull(message = "Escolha o tipo")
    private TipoEndereco tipo = TipoEndereco.RESIDENCIAL;

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public TipoEndereco getTipo() {
        return tipo;
    }

    public void setTipo(TipoEndereco tipo) {
        this.tipo = tipo;
    }
}
