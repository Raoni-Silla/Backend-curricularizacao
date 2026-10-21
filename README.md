# Métodos Ágeis na Prática · API

API do sistema de prova interativa sobre metodologias ágeis, desenvolvido como projeto de curricularização da extensão do curso de Análise e Desenvolvimento de Sistemas da FEMA (Assis, SP).

O público responde a uma prova curta pelo celular, informando antes o próprio nível de experiência com métodos ágeis. Enquanto isso, a equipe acompanha os resultados em um painel administrativo, encerra a prova ao vivo e exporta um relatório em PDF comparando o desempenho por nível de experiência.

**Aplicação no ar:** [metodos-ageis-chi.vercel.app](https://metodos-ageis-chi.vercel.app)
**API:** `https://metodos-ageis-api.onrender.com/api`
**Frontend:** [Frontend-curricularizacao](https://github.com/Raoni-Silla/Frontend-curricularizacao)

---

## Funcionalidades

**Participante**

- Inicia a prova informando nome, idade, sexo, escolaridade e experiência com métodos ágeis
- Recebe um token JWT vinculado à própria tentativa
- Envia as respostas e recebe o número de acertos ao finalizar

**Administrador**

- Login com nome de usuário e senha (BCrypt)
- Painel com totais de participantes, tentativas em andamento, finalizadas e expiradas
- Média de acertos geral e por nível de experiência
- Ativação e encerramento da prova
- Relatório em PDF gerado com JasperReports

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Segurança | Spring Security, JWT, BCrypt |
| Persistência | Spring Data JPA, Hibernate 7 |
| Banco de dados | PostgreSQL 18 |
| Relatórios | JasperReports 6.21 |
| Utilitários | Lombok, Bean Validation |
| Infraestrutura | Docker, Render, Neon |

---

## Regras de negócio

- **Prova ativa:** novas tentativas só podem ser iniciadas enquanto a prova estiver ativa.
- **Encerramento:** ao encerrar a prova, as tentativas ainda em andamento passam para `EXPIRADA`, com o mesmo horário de fim para todas.
- **Respostas:** são gravadas no momento em que o participante finaliza. Tentativas que não estão `EM_ANDAMENTO` não aceitam envio.
- **Média de acertos:** considera apenas tentativas `FINALIZADA`. Participantes que expiraram contam no total, mas não afetam a média.
- **Separação de perfis:** alunos e administradores compartilham a entidade `Usuario`, diferenciados pelo `TipoUsuario`. Os indicadores do painel consideram apenas alunos.

---

## Endpoints

### Participante · `/api/exam`

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/provaAtiva` | Público | Informa se a prova está aberta |
| `POST` | `/iniciarProva` | Público | Cadastra o participante, cria a tentativa e devolve o token |
| `GET` | `/carregarProva/{idTentativa}` | Aluno | Retorna as perguntas e alternativas |
| `PATCH` | `/finalizarProva` | Aluno | Registra as respostas e devolve o número de acertos |

### Administração · `/api/admin`

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/autenticar` | Público | Login do administrador |
| `GET` | `/dashboard` | Admin | Indicadores, desempenho por experiência e lista de tentativas |
| `PATCH` | `/ativar` | Admin | Ativa a prova e devolve o painel atualizado |
| `PATCH` | `/desativar` | Admin | Encerra a prova, expira as tentativas abertas e devolve o painel |
| `GET` | `/relatorio` | Admin | Gera o relatório em PDF |

As rotas protegidas exigem o cabeçalho `Authorization: Bearer <token>`. O papel do usuário (`ALUNO` ou `ADMIN`) vem na claim `ROLE` do token e é verificado com `@PreAuthorize`.

### Formato de erro

Os erros são padronizados por um `@RestControllerAdvice`:

```json
{
  "status": 400,
  "mensagem": "Esta prova foi encerrada pelo organizador",
  "detalhes": [],
  "timestamp": "2026-10-24T09:41:12.512"
}
```

---

## Executando localmente

### Pré-requisitos

- Java 21
- Maven 3.9 ou superior
- PostgreSQL 18

### Passos

1. Crie o banco:

   ```sql
   CREATE DATABASE sistemametodosageis;
   ```

2. Rode a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

O Hibernate cria as tabelas na primeira execução (`ddl-auto=update`). A API fica disponível em `http://localhost:8080`.

As configurações têm valores padrão para desenvolvimento local, então nenhuma variável de ambiente é obrigatória nessa etapa.

### Com Docker

```bash
docker build -t metodos-ageis-backend .
docker run --rm --network host metodos-ageis-backend
```

O `--network host` permite que o container acesse o PostgreSQL da máquina local.

---

## Variáveis de ambiente

| Variável | Descrição | Padrão local |
|---|---|---|
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/sistemametodosageis` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `JWT_SECRET` | Chave de assinatura dos tokens, com pelo menos 32 caracteres | chave de desenvolvimento |
| `CORS_ORIGINS` | Origens permitidas, separadas por vírgula | `http://localhost:4200,http://*:4200` |
| `PORT` | Porta HTTP | `8080` |
| `SHOW_SQL` | Exibe as consultas SQL no log | `false` |

Em produção, gere a chave do JWT com:

```bash
openssl rand -base64 48
```

---

## Criando um administrador

Não existe cadastro público de administradores. O primeiro é inserido direto no banco, com a senha criptografada em BCrypt pela extensão `pgcrypto`, compatível com o `BCryptPasswordEncoder` do Spring:

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO usuario (nome, idade, sexo, escolaridade, experiencia, tipo_usuario, senha)
VALUES ('admin', 20, 'MASCULINO', 'SUPERIOR', 'PROFISSIONAL', 'ADMIN',
        crypt('SENHA_FORTE_AQUI', gen_salt('bf', 10)));
```

O login diferencia maiúsculas e minúsculas no nome de usuário.

---

## Deploy

| Componente | Serviço |
|---|---|
| API | Render (Web Service com Docker, região Ohio) |
| Banco de dados | Neon (PostgreSQL 18, região AWS US East 2) |

O `Dockerfile` usa build em duas etapas e já resolve três pontos do ambiente de produção:

- **Fontes:** instala o `fontconfig`, necessário para o JasperReports gerar o PDF em imagens Linux enxutas.
- **Fuso horário:** define `America/Sao_Paulo`, para os horários das tentativas não ficarem em UTC.
- **Memória:** limita o Java a 75% da memória do container, dentro dos 512 MB do plano gratuito.

No plano gratuito do Render, o serviço é suspenso após 15 minutos sem tráfego e leva cerca de um minuto para voltar. Antes de uma apresentação, acesse o painel e gere um PDF para aquecer a API e o banco.

---

## Estrutura

```
src/main/java/fema/metodosageis/SistemaMetodosAgeis
├── config/       # Spring Security, filtro JWT e CORS
├── controller/   # Endpoints REST
├── dto/          # Objetos de entrada e saída, incluindo os do painel
├── enums/        # Escolaridade, Experiencia, Sexo, StatusProva, TipoUsuario
├── exception/    # Tratamento global de erros
├── model/        # Entidades JPA
├── repository/   # Repositórios Spring Data
└── service/      # Regras de negócio e geração do relatório

src/main/resources
├── application.properties
└── relatorios/relatorio-prova.jrxml
```

---

## Autor

**Raoní Mendes Silla**
[GitHub](https://github.com/Raoni-Silla) · [LinkedIn](https://linkedin.com/in/raoní-mendes-silla-14a2122b5)

<!-- Equipe: adicione aqui os demais integrantes do grupo -->

Projeto de curricularização da extensão · Análise e Desenvolvimento de Sistemas · FEMA, 2026
