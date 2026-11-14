# Multiloja: plataforma de e-commerce multi-loja

Trabalho da disciplina **Tópicos em Engenharia de Software** (UFSCar, Prof. Dr. Delano Medeiros Beder).
Grupo: Miguel Monteiro Parro, Vinicius Luiz Teixeira e Guilherme Veronezi Costa.

Sistema web onde **lojas** cadastram categorias, produtos (com variações de cor e tamanho), estoque e cupons, e **clientes** montam carrinho, finalizam pedidos com pagamento e avaliam o que compraram.

## Tecnologias

| Item | Tecnologia |
|---|---|
| Linguagem / framework | Java 17+ e Spring Boot 3.3 |
| Camada web (View) | Thymeleaf |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | **PostgreSQL 16** |
| Autenticação | **Spring Security** (login por formulário, senhas com **BCrypt**, perfis CLIENTE e LOJA) |
| Build | Maven |

## Pré-requisitos

- **JDK 17 ou superior** (recomendado 21). Confira com `java -version`.
- **Maven 3.9+**. Confira com `mvn -v`.
- **Git**.
- **PostgreSQL**: pelo **Docker** (opção A, recomendada) ou instalado na máquina (opção B).

No Windows, o que faltar pode ser instalado com:
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install Apache.Maven
winget install Git.Git
```
Feche e reabra o terminal depois de instalar.

## Passo a passo para executar localmente

### 1. Baixar o código
```bash
git clone https://github.com/migle-l/multiloja.git
cd multiloja
```

### 2. Subir o banco de dados PostgreSQL

A aplicação espera um banco `multiloja`, com usuário `multiloja` e senha `multiloja`, em `localhost:5432`.

**Opção A: Docker** (o `docker-compose.yml` já cria tudo):
```bash
docker compose up -d
docker compose ps
```
O contêiner `multiloja-db` deve aparecer como `running`.

**Opção B: PostgreSQL instalado na máquina** (sem Docker):
```powershell
winget install PostgreSQL.PostgreSQL.16
```
Durante a instalação, defina a senha do usuário `postgres` e mantenha a porta `5432`. Depois crie o banco e o usuário:
```powershell
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -f scripts\criar-banco.sql
```
(em Linux/macOS: `psql -U postgres -f scripts/criar-banco.sql`)

### 3. Iniciar a aplicação
Na pasta do projeto (a que contém o `pom.xml`):
```bash
mvn spring-boot:run
```
Na primeira execução o Maven baixa as dependências (alguns minutos). As tabelas são criadas automaticamente pelo Hibernate e, com o banco vazio, são inseridos dados de exemplo. Aguarde a linha `Started MultilojaApplication`.

### 4. Acessar
Abra **http://localhost:8080**.

| Perfil | E-mail | Senha |
|---|---|---|
| Loja | `loja@exemplo.com` | `senha12345` |
| Cliente | `cliente@exemplo.com` | `senha12345` |

Cupom de exemplo: `CAMISA10` (10% em produtos da categoria Camisetas).
Também é possível criar contas em `/cadastro/cliente` (CPF válido) e `/cadastro/loja` (CNPJ válido).
CPF válido para teste: `111.444.777-35`. CNPJ válido para teste: `11.444.777/0001-61`.

### 5. Encerrar
`Ctrl + C` no terminal da aplicação e, se usou Docker, `docker compose down`.
Para apagar também os dados do banco e recomeçar do zero: `docker compose down -v`.

## Configuração do banco

Os padrões ficam em `src/main/resources/application.properties` e podem ser trocados por variáveis de ambiente, sem editar o código:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/multiloja` |
| `DB_USER` | `multiloja` |
| `DB_PASSWORD` | `multiloja` |

## Consultando o banco

Com Docker:
```bash
docker exec -it multiloja-db psql -U multiloja -d multiloja
```
Sem Docker: `psql -U multiloja -d multiloja -h localhost`. Também funcionam pgAdmin ou DBeaver com os mesmos dados de conexão.

```sql
\dt
SELECT id, nome, email FROM lojas;
SELECT id, nome, email FROM clientes;
SELECT p.id, p.codigo, p.nome, p.preco_base, l.nome AS loja, c.nome AS categoria
  FROM produtos p JOIN lojas l ON l.id = p.loja_id JOIN categorias c ON c.id = p.categoria_id;
SELECT * FROM variacoes_produto;
SELECT * FROM cupons;
SELECT numero, status, subtotal, desconto, total FROM pedidos;
SELECT * FROM pagamentos;
SELECT * FROM avaliacoes;
```
A coluna `senha_hash` das tabelas `clientes` e `lojas` mostra o hash BCrypt (`$2a$...`).

## Autenticação e segurança

- **Spring Security + Thymeleaf**: tela de login própria (`/login`), logout, proteção CSRF nos formulários e autorização por perfil (`/loja/**` só para LOJA; carrinho, checkout, pedidos e endereços só para CLIENTE).
- **Senhas criptografadas** com BCrypt (`SecurityConfig`); nenhuma senha é gravada em texto puro.
- O login é feito por e-mail para os dois perfis (`UsuarioDetailsService`).
- O sistema não armazena dados de cartão: guarda apenas forma, valor e status do pagamento.

## Arquitetura MVC

| Camada | Pasta | Conteúdo |
|---|---|---|
| Model (domínio) | `domain/` | Entidades JPA e regras próprias do objeto (`Carrinho.getTotal()`, `Cupom.calcularDesconto()`) |
| Model (persistência) | `repository/` | Interfaces Spring Data JPA |
| Regras de negócio | `service/` | Cadastro, catálogo, carrinho, pedido, cupom e avaliação (transações) |
| Controller | `controller/` | Controladores Spring MVC |
| View | `resources/templates/` | Páginas Thymeleaf (`loja/` é a área da loja) |
| Apoio | `form/`, `validation/`, `security/`, `config/`, `util/` | Formulários, validadores de CPF/CNPJ, login e dados iniciais |

## Requisitos implementados

| Requisito | Onde |
|---|---|
| Cadastro com CPF / CNPJ (validados) | `AuthController`, `CadastroService`, `@Cpf`, `@Cnpj` |
| RF001 login de cliente e loja | `SecurityConfig`, `UsuarioDetailsService` |
| RF002 múltiplos endereços | `EnderecoController`, `EnderecoService` |
| RF003 categorias | `LojaController`, `CatalogoService` |
| RF004 produtos | `LojaProdutoController`, `CatalogoService` |
| RF005 variações (cor, tamanho, preço adicional, estoque) | `LojaProdutoController`, `VariacaoProduto` |
| RF006 / RN001 carrinho único por cliente | `Carrinho` (1:1 com `Cliente`) |
| RF007 / RN002 itens por variação | `CarrinhoController`, `ItemCarrinho` |
| RF008 / RN005 cupom por categoria | `CarrinhoService.aplicarCupom`, `Cupom` |
| RF009 / RF010 pedido com endereço de entrega | `PedidoService.finalizar` |
| RF011 / RN003 pagamentos (até 2 por pedido na tela) | `Pagamento`, `CheckoutForm` |
| RF012 / RN004 avaliação de item comprado | `AvaliacaoController`, `AvaliacaoService` |
| RNF001 interface responsiva | `static/css/estilo.css` |
| RNF002 senhas criptografadas | BCrypt (Spring Security) |
| RNF003 consulta rápida | busca paginada (12 por página) |

## Problemas comuns

| Sintoma | Causa e solução |
|---|---|
| `Connection to localhost:5432 refused` | O PostgreSQL não está rodando. Suba o banco (passo 2) |
| `password authentication failed` | Usuário ou senha diferentes dos padrões. Crie o usuário `multiloja` ou ajuste `DB_USER` / `DB_PASSWORD` |
| `Port 8080 was already in use` | Há outra aplicação na porta. Encerre-a ou rode com `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081` |
| `mvn` ou `java` não reconhecido | Instale o JDK / Maven e reabra o terminal |
| Porta 5432 já em uso ao subir o Docker | Já existe um PostgreSQL local. Use-o (opção B) ou pare-o |

## Limitações

- O pagamento é simulado: cartão aprova na hora; Pix e boleto ficam pendentes até a loja confirmar no painel.
- Um pedido com itens de várias lojas é um único pedido (sem divisão por loja nem frete).
- Endereços podem ser criados e removidos, mas não editados. Não há upload de imagens nem testes automatizados.
