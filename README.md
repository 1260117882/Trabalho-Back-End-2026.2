# Trabalho-Back-end
Projeto a ser apresentado como requisito de nota trimestral para a disciplina Back End, cujo objetivo é desenvolver uma plataforma online de filmes que ofereça aos usuários acesso rápido e prático a um catálogo variado de obras cinematográficas, com navegação intuitiva e opção de aluguel temporário.

# Tecnologias

- **Banco de dados:** PostgreSQL 16
- **Back-end:** Java 17 + Spring Boot (API REST)
- **Front-end:** HTML, CSS, Bootstrap e Node.js/Express
- **Infraestrutura:** Docker e Docker Compose

# Estrutura do projeto

```
├── docker-compose.yml     # orquestra os 3 containers
├── .env.example           # modelo das variáveis de ambiente
├── database/init/         # scripts SQL executados na criação do banco
├── backend/               # API Spring Boot (+ Dockerfile)
└── frontend/              # servidor Node (+ Dockerfile) e páginas em public/
```

# Pré-requisitos

- Docker Desktop instalado e aberto (no Windows)
- Git

# Como executar

1. Clone o repositório e entre na pasta:
```bash
   git clone https://github.com/1260117882/Trabalho-Back-End-2026.2.git
   cd Trabalho-Back-End-2026.2
```
2. Crie o arquivo `.env` a partir do modelo:
```bash
   cp .env.example .env
```
   No PowerShell: `Copy-Item .env.example .env`
3. Preencha o `.env` com:
```
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   POSTGRES_DB=rentafilm
```
4. Suba tudo:
```bash
   docker compose up --build
```

# Endereços

| Serviço | URL |
|---|---|
| Site (frontend) | http://localhost:3000 |
| API (backend) | http://localhost:8080/api/filmes |
| Documentação Swagger | http://localhost:8080/swagger-ui.html |
| Banco (acesso externo) | localhost:5432 |

# Comandos Docker

| Comando | O que faz |
|---|---|
| `docker compose config` | Valida o `docker-compose.yml` e mostra as variáveis já substituídas |
| `docker compose up --build` | Constrói as imagens e sobe os 3 containers, com logs no terminal |
| `docker compose up -d --build` | Igual ao anterior, mas em segundo plano |
| `docker compose ps` | Lista os containers e o estado (`healthy`, `Up`) |
| `docker compose logs -f backend` | Acompanha os logs de um serviço (Ctrl+C para sair) |
| `docker compose restart backend` | Reinicia apenas um serviço |
| `docker compose exec db psql -U postgres -d rentafilm -c "\dt"` | Lista as tabelas do banco |
| `docker compose down` | Para e remove os containers, mantendo os dados do banco |
| `docker compose down -v` | Remove também o volume: o banco é recriado do zero |

> Os scripts de `database/init/` só rodam quando o banco é criado pela primeira vez. Se alterar os `.sql`, use `docker compose down -v` e suba de novo.

# Como funciona a subida

O `docker-compose.yml` define uma ordem de inicialização:

1. **db** sobe primeiro e só fica `healthy` quando o PostgreSQL aceita conexões.
2. **backend** espera o banco ficar saudável. Dentro do Docker ele acessa o banco pelo host `db`, não `localhost`.
3. **frontend** espera o backend ficar saudável.

# Equipe

- (nome e função)
