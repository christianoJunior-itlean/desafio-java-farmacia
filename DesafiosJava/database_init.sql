-- Script SQL para inicialização do banco de dados

-- Criar o banco de dados (executar como superusuário)
-- CREATE DATABASE farmacia_db;

-- Conectar ao banco farmacia_db antes de executar os comandos abaixo

-- As tabelas serão criadas automaticamente pelo Hibernate (ddl-auto=update)
-- Este script apenas demonstra a estrutura esperada

-- Inserir usuário padrão (senha: admin123)
-- A senha será criptografada pela aplicação
-- Execute via endpoint POST /auth/registrar com:
-- { "username": "admin", "senha": "admin123" }

-- Exemplo de inserção manual de categorias (opcional - pode ser feito via API)
-- INSERT INTO categorias (nome) VALUES 
--   ('Analgésicos'),
--   ('Antibióticos'),
--   ('Anti-inflamatórios'),
--   ('Vitaminas');

-- Verificar tabelas criadas
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public'
ORDER BY table_name;
