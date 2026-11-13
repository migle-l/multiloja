# Multiloja (Spring Boot, Thymeleaf e JPA)

Plataforma de e-commerce multi-loja baseada no Documento de Requisitos (Tópicos em Engenharia de Software, UFSCar).

## Arquitetura MVC

| Camada | Pacote / pasta | Conteúdo |
|---|---|---|
| **Model** (domínio) | `domain/` | Entidades JPA e regras que pertencem ao objeto (ex.: `Carrinho.getTotal()`, `Cupom.calcularDesconto()`) |
| **Model** (persistência) | `repository/` | Interfaces Spring Data JPA |
| Regras de negócio | `service/` | Cadastro, catálogo, carrinho, pedido, cupom, avaliação (transações) |
| **Controller** | `controller/` | Controladores Spring MVC (rotas, validação, redirecionamentos) |
| **View** | `resources/templates/` | Páginas Thymeleaf (`loja/` = área da loja) |
| Apoio | `form/`, `validation/`, `security/`, `config/`, `util/` | DTOs de formulário, @Cpf/@Cnpj, login, dados iniciais |

## Como rodar

Pré-requisitos: **JDK 17+** (recomendado 21) e **Maven**.

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install Apache.Maven
```
(feche e reabra o VS Code depois; confira com `java -version` e `mvn -v`)

```powershell
mvn spring-boot:run
```
Acesse **http://localhost:8080**. O banco H2 é criado em `./data/` e populado na primeira execução.

Logins de exemplo (senha `senha12345`): `loja@exemplo.com` (loja) e `cliente@exemplo.com` (cliente). Cupom: `CAMISA10` (10% em Camisetas).

Para PostgreSQL: troque a dependência `h2` por `org.postgresql:postgresql` no `pom.xml` e ajuste `spring.datasource.*` no `application.properties`.

## Requisitos implementados

| Requisito | Onde |
|---|---|
| Cadastro com CPF / CNPJ (validados) | `AuthController`, `CadastroService`, `@Cpf`, `@Cnpj` |
| RF001 login cliente e loja | `SecurityConfig`, `UsuarioDetailsService` |
| RF002 múltiplos endereços | `EnderecoController`, `EnderecoService` |
| RF003 categorias | `LojaController`, `CatalogoService` |
| RF004 produtos | `LojaProdutoController`, `CatalogoService` |
| RF005 variações (cor, tamanho, preço adicional, estoque) | `LojaProdutoController`, `VariacaoProduto` |
| RF006 / RN001 carrinho único | `Carrinho` (1:1 com `Cliente`) |
| RF007 / RN002 itens por variação | `CarrinhoController`, `ItemCarrinho` |
| RF008 / RN005 cupom por categoria | `CarrinhoService.aplicarCupom`, `Cupom` |
| RF009 / RF010 pedido com endereço | `PedidoService.finalizar` |
| RF011 / RN003 pagamentos (até 2 por pedido na tela) | `Pagamento`, `CheckoutForm` |
| RF012 / RN004 avaliação de item comprado | `AvaliacaoController`, `AvaliacaoService` |
| RNF001 interface responsiva | `static/css/estilo.css` |
| RNF002 senhas criptografadas | BCrypt; o sistema não armazena dados de cartão |
| RNF003 consulta rápida | busca paginada (12 por página) |

## Limitações conhecidas

- Pagamento é simulado: cartão aprova na hora; Pix/boleto ficam pendentes até a loja confirmar no painel.
- Um pedido com itens de várias lojas é um único pedido (sem divisão por loja nem frete).
- Endereços só podem ser criados e removidos (sem edição); não há upload de imagens nem testes automatizados.
