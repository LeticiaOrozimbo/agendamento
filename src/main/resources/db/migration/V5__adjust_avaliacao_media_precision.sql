-- Alterar o tipo de dados para suportar valores maiores
ALTER TABLE estabelecimentos
ALTER COLUMN avaliacao_media TYPE NUMERIC(4,2);

ALTER TABLE profissionais
ALTER COLUMN avaliacao_media TYPE NUMERIC(4,2);
