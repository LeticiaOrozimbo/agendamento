-- Clientes
CREATE TABLE clientes (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(2),
    cep VARCHAR(8),
    calendario_integrado BOOLEAN DEFAULT FALSE,
    email_calendario VARCHAR(255)
);

-- Estabelecimentos
CREATE TABLE estabelecimentos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL
);

-- Fotos do estabelecimento
CREATE TABLE estabelecimento_fotos (
    estabelecimento_id UUID REFERENCES estabelecimentos(id),
    url VARCHAR(255) NOT NULL,
    PRIMARY KEY (estabelecimento_id, url)
);

-- Horários de funcionamento do estabelecimento
CREATE TABLE estabelecimento_horarios (
    estabelecimento_id UUID REFERENCES estabelecimentos(id),
    dia_semana INTEGER NOT NULL,
    horario_abertura TIME NOT NULL,
    horario_fechamento TIME NOT NULL,
    PRIMARY KEY (estabelecimento_id, dia_semana)
);

-- Serviços do estabelecimento
CREATE TABLE estabelecimento_servicos (
    estabelecimento_id UUID REFERENCES estabelecimentos(id),
    servico_id UUID NOT NULL,
    PRIMARY KEY (estabelecimento_id, servico_id)
);

-- Profissionais do estabelecimento
CREATE TABLE estabelecimento_profissionais (
    estabelecimento_id UUID REFERENCES estabelecimentos(id),
    profissional_id UUID NOT NULL,
    PRIMARY KEY (estabelecimento_id, profissional_id)
);

-- Profissionais
CREATE TABLE profissionais (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    especialidades TEXT,
    foto VARCHAR(255),
    tarifa_base DECIMAL(10,2) NOT NULL,
    calendario_integrado BOOLEAN DEFAULT FALSE,
    email_calendario VARCHAR(255),
    email VARCHAR(255)
);

-- Disponibilidade dos profissionais
CREATE TABLE profissional_disponibilidade (
    profissional_id UUID NOT NULL REFERENCES profissionais(id),
    dia_semana INTEGER NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    PRIMARY KEY (profissional_id, dia_semana, horario_inicio, horario_fim),
    CONSTRAINT fk_disponibilidade_profissional FOREIGN KEY (profissional_id)
        REFERENCES profissionais(id) ON DELETE CASCADE
);

-- Serviços
CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    duracao INTEGER NOT NULL -- duração em minutos
);

-- Relação entre profissionais e serviços
CREATE TABLE profissional_servicos (
    profissional_id UUID REFERENCES profissionais(id),
    servico_id UUID REFERENCES servicos(id),
    PRIMARY KEY (profissional_id, servico_id)
);

-- Avaliações
CREATE TABLE avaliacoes (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL REFERENCES clientes(id),
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id),
    profissional_id UUID REFERENCES profissionais(id),
    estrelas INTEGER NOT NULL CHECK (estrelas BETWEEN 1 AND 5),
    comentario TEXT,
    data_avaliacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avaliacao_estabelecimento FOREIGN KEY (estabelecimento_id)
        REFERENCES estabelecimentos(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_profissional FOREIGN KEY (profissional_id)
        REFERENCES profissionais(id) ON DELETE SET NULL
);

-- Agendamentos
CREATE TABLE agendamentos (
    id UUID PRIMARY KEY,
    cliente_id UUID REFERENCES clientes(id),
    estabelecimento_id UUID REFERENCES estabelecimentos(id),
    profissional_id UUID REFERENCES profissionais(id),
    servico_id UUID REFERENCES servicos(id),
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_agendamentos_cliente ON agendamentos(cliente_id);
CREATE INDEX idx_agendamentos_profissional ON agendamentos(profissional_id);
CREATE INDEX idx_agendamentos_estabelecimento ON agendamentos(estabelecimento_id);
CREATE INDEX idx_avaliacoes_estabelecimento ON avaliacoes(estabelecimento_id);
CREATE INDEX idx_avaliacoes_profissional ON avaliacoes(profissional_id);
CREATE INDEX idx_servicos_categoria ON servicos(categoria);
