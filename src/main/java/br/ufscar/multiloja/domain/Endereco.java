package br.ufscar.multiloja.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "enderecos")
public class Endereco {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @Column(nullable = false)
    private String logradouro;
    private String complemento;
    @Column(nullable = false)
    private String bairro;
    @Column(nullable = false)
    private String cidade;
    @Column(nullable = false, length = 2)
    private String estado;
    @Column(nullable = false, length = 9)
    private String cep;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoEndereco tipo = TipoEndereco.RESIDENCIAL;
    /** Exclusão lógica: pedidos antigos continuam apontando para o endereço. */
    private boolean ativo = true;

    public String getResumo() {
        String comp = (complemento == null || complemento.isBlank()) ? "" : ", " + complemento;
        return logradouro + comp + " - " + bairro + ", " + cidade + "/" + estado + " - CEP " + cep;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
