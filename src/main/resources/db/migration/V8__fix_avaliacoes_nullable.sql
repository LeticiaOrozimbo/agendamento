-- Ajustar tabela avaliacoes para permitir avaliação separada de estabelecimentos e profissionais
-- Tornar estabelecimento_id e profissional_id opcionais

-- Remover constraint NOT NULL de estabelecimento_id
ALTER TABLE avaliacoes ALTER COLUMN estabelecimento_id DROP NOT NULL;

-- Adicionar constraint para garantir que pelo menos um dos dois seja preenchido
ALTER TABLE avaliacoes ADD CONSTRAINT avaliacoes_target_check
CHECK (estabelecimento_id IS NOT NULL OR profissional_id IS NOT NULL);
