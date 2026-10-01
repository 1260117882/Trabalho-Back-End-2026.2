-- =====================================================================
-- data.sql
-- Dados de teste para a tabela usuarios (execute DEPOIS do schema.sql).
-- As senhas são geradas com crypt() + gen_salt('bf') (pgcrypto), que produz
-- hash bcrypt, o mesmo padrão que o back-end deve usar.
-- Senha de todos os usuários de teste: Senha@123
-- =====================================================================

INSERT INTO usuarios (nome_completo, email, data_nascimento, telefone, senha_hash)
VALUES
    -- Colunas: nome_completo, email (login), data_nascimento, telefone, senha_hash
    ('Ana Beatriz Souza',   'ana.souza@email.com',    '1998-03-15', '21987654321',  crypt('Senha@123', gen_salt('bf'))),
    ('Bruno Henrique Lima', 'bruno.lima@email.com',   '1995-11-02', '21991234567',  crypt('Senha@123', gen_salt('bf'))),
    ('Carla Mendes Rocha',  'carla.rocha@email.com',  '2001-07-28', '11988776655',  crypt('Senha@123', gen_salt('bf'))),
    ('Diego Alves Pereira', 'diego.alves@email.com',  '1990-01-09', '31977665544',  crypt('Senha@123', gen_salt('bf'))),
    ('Eduarda Costa Nunes', 'eduarda.nunes@email.com','2003-09-21', '+5521999887766', crypt('Senha@123', gen_salt('bf')));

-- Usuário de teste desativado (para testar o bloqueio de login de contas inativas).
INSERT INTO usuarios (nome_completo, email, data_nascimento, telefone, senha_hash, ativo)
VALUES
    ('Fábio Inativo Teste', 'fabio.inativo@email.com', '1988-05-30', '21955443322', crypt('Senha@123', gen_salt('bf')), FALSE);

-- ---------------------------------------------------------------------
-- Consulta de exemplo para validar o login (senha correta retorna 1 linha):
-- SELECT id, nome_completo FROM usuarios
--  WHERE LOWER(email) = LOWER('ana.souza@email.com')
--    AND senha_hash = crypt('Senha@123', senha_hash)
--    AND ativo = TRUE;
-- ---------------------------------------------------------------------