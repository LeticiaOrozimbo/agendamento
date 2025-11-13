-- Script para verificar agendamentos no banco
SELECT
    id,
    cliente_id,
    profissional_id,
    servico_id,
    estabelecimento_id,
    inicio,
    fim,
    status,
    criado_em
FROM agendamentos
WHERE profissional_id = '441f94c5-c8fd-4cf5-96b2-66d624d03227'
ORDER BY inicio;

-- Verificar todos os agendamentos
SELECT COUNT(*) as total_agendamentos FROM agendamentos;

-- Verificar agendamentos por status
SELECT status, COUNT(*) FROM agendamentos GROUP BY status;
