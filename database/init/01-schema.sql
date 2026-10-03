-- =====================================================================
-- schema.sql
-- Criação da estrutura do banco de dados (PostgreSQL)
-- Origem dos campos: telas de Login e Cadastro do front-end.
-- =====================================================================

-- Extensão para funções de criptografia (usada no data.sql para gerar hash de senha).
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Remove a tabela caso já exista, permitindo rodar o script várias vezes.
DROP TABLE IF EXISTS usuarios;

-- ---------------------------------------------------------------------
-- TABELA: usuarios
-- Finalidade: armazena as pessoas cadastradas no sistema. É usada
-- na tela de Cadastro (INSERT) e na tela de Login (SELECT por e-mail,
-- comparando a senha informada com o hash armazenado).
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (

    -- Identificador único e automático de cada usuário (chave primária).
    id              BIGSERIAL PRIMARY KEY,

    -- Cadastro: "Nome Completo". Nome da pessoa como digitado no formulário.
    nome_completo   VARCHAR(150) NOT NULL,

    -- Cadastro/Login: "Email". Serve como identificador de acesso (login),
    -- por isso não pode se repetir (ver índice único abaixo).
    email           VARCHAR(255) NOT NULL,

    -- Cadastro: "Data de Nascimento". Apenas a data, sem horário.
    data_nascimento DATE NOT NULL,

    -- Cadastro: "Telefone". Guardado como texto (e não número) para
    -- preservar o zero à esquerda, o "+" e o DDD. Aceita de 10 a 15 dígitos.
    telefone        VARCHAR(20) NOT NULL,

    -- Cadastro/Login: "Senha". NUNCA guardamos a senha pura: aqui fica
    -- apenas o hash (ex.: bcrypt), gerado pelo back-end antes do INSERT.
    senha_hash      VARCHAR(255) NOT NULL,

    -- Indica se a conta está ativa. Permite desativar sem apagar o registro.
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,

    -- Data/hora em que o cadastro foi criado (preenchida automaticamente).
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Data/hora da última alteração (o back-end deve atualizar em UPDATEs).
    atualizado_em   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Validação básica de formato do e-mail.
    CONSTRAINT ck_usuarios_email_formato
        CHECK (email ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$'),

    -- Telefone: somente dígitos, com "+" opcional no início (10 a 15 dígitos).
    CONSTRAINT ck_usuarios_telefone_formato
        CHECK (telefone ~ '^\+?[0-9]{10,15}$'),

    -- Data de nascimento dentro de um intervalo plausível. A checagem de
    -- "não pode ser futura" deve ser feita também no back-end, pois CHECK
    -- com CURRENT_DATE não é recomendado (valor muda com o tempo).
    CONSTRAINT ck_usuarios_data_nascimento
        CHECK (data_nascimento >= DATE '1900-01-01' AND data_nascimento <= DATE '2100-01-01')
);

-- Garante e-mail único sem diferenciar maiúsculas/minúsculas
-- (ex.: "Ana@x.com" e "ana@x.com" são o mesmo e-mail). Também acelera o login.
CREATE UNIQUE INDEX uq_usuarios_email_lower ON usuarios (LOWER(email));
