-- Script para resetar o banco de dados
-- Execute este script no DBeaver ou pgAdmin

-- Dropar todas as tabelas
DROP TABLE IF EXISTS agendamentos CASCADE;
DROP TABLE IF EXISTS avaliacoes CASCADE;
DROP TABLE IF EXISTS profissional_servicos CASCADE;
DROP TABLE IF EXISTS profissional_disponibilidade CASCADE;
DROP TABLE IF EXISTS estabelecimento_profissionais CASCADE;
DROP TABLE IF EXISTS estabelecimento_servicos CASCADE;
DROP TABLE IF EXISTS estabelecimento_horarios CASCADE;
DROP TABLE IF EXISTS estabelecimento_fotos CASCADE;
DROP TABLE IF EXISTS servicos CASCADE;
DROP TABLE IF EXISTS profissionais CASCADE;
DROP TABLE IF EXISTS estabelecimentos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- Limpar histórico do Flyway
DELETE FROM flyway_schema_history WHERE version = '1';

