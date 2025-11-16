# Sistema de Agendamento - Documentação da API

## Visão Geral

Esta é a documentação completa da API RESTful do Sistema de Agendamento para estabelecimentos de beleza e bem-estar. A API segue os princípios REST e utiliza JSON para troca de dados.

### Informações da API
- **Versão**: 1.0.0
- **URL Base (Desenvolvimento)**: `http://localhost:8080`
- **URL Base (Produção)**: `https://sistema-agendamento.onrender.com`
- **Formato de Dados**: JSON
- **Autenticação**: JWT Bearer Token

### Acesso à Documentação Interativa
- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI JSON**: `/v3/api-docs`

---

## Modelos de Dados

### EstabelecimentoDTO
```json
{
  "nome": "string",
  "descricao": "string",
  "endereco": {
    "logradouro": "string",
    "numero": "string",
    "complemento": "string",
    "bairro": "string",
    "cidade": "string",
    "estado": "string",
    "cep": "string"
  },
  "fotos": ["string"],
  "horariosFuncionamento": [
    {
      "diaSemana": "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
      "horarioAbertura": "HH:mm",
      "horarioFechamento": "HH:mm"
    }
  ]
}
```

### ClienteDTO
```json
{
  "nome": "string",
  "email": "string",
  "telefone": "string",
  "endereco": {
    "logradouro": "string",
    "numero": "string",
    "complemento": "string",
    "bairro": "string",
    "cidade": "string",
    "estado": "string",
    "cep": "string"
  }
}
```

### ProfissionalDTO
```json
{
  "nome": "string",
  "especialidades": "string",
  "foto": "string",
  "tarifaBase": 0.0,
  "servicosIds": ["uuid"],
  "disponibilidade": [
    {
      "diaSemana": "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
      "horarioInicio": "HH:mm",
      "horarioFim": "HH:mm"
    }
  ]
}
```

### ServicoDTO
```json
{
  "nome": "string",
  "descricao": "string",
  "categoria": "string",
  "preco": 0.0,
  "duracaoMinutos": 0
}
```

### AgendamentoDTO
```json
{
  "clienteId": "uuid",
  "estabelecimentoId": "uuid",
  "profissionalId": "uuid",
  "servicoId": "uuid",
  "inicio": "2024-11-17T10:30:00-03:00"
}
```

---

## Endpoints da API

## 1. Estabelecimentos

### POST /api/estabelecimentos
**Criar novo estabelecimento**

Cadastra um novo estabelecimento de beleza e bem-estar no sistema.

**Request Body:**
```json
{
  "nome": "Salão Beleza Mais",
  "descricao": "Salão de beleza completo com serviços de cabelo, unha e estética",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Sala 1",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01234567"
  },
  "fotos": [
    "https://exemplo.com/foto1.jpg",
    "https://exemplo.com/foto2.jpg"
  ],
  "horariosFuncionamento": [
    {
      "diaSemana": "MONDAY",
      "horarioAbertura": "09:00",
      "horarioFechamento": "18:00"
    },
    {
      "diaSemana": "SATURDAY",
      "horarioAbertura": "09:00",
      "horarioFechamento": "16:00"
    }
  ]
}
```

**Responses:**
- `200 OK`: Estabelecimento criado com sucesso
- `400 Bad Request`: Dados inválidos fornecidos
- `409 Conflict`: Estabelecimento já existe

---

### GET /api/estabelecimentos
**Listar todos os estabelecimentos**

Retorna a lista completa de estabelecimentos cadastrados.

**Responses:**
- `200 OK`: Lista de estabelecimentos

---

### GET /api/estabelecimentos/{id}
**Buscar estabelecimento por ID**

Retorna os detalhes de um estabelecimento específico.

**Parameters:**
- `id` (path, required): ID único do estabelecimento

**Responses:**
- `200 OK`: Estabelecimento encontrado
- `404 Not Found`: Estabelecimento não encontrado

---

### GET /api/estabelecimentos/buscar
**Buscar estabelecimentos com filtros**

Busca estabelecimentos aplicando filtros como nome, localização, serviços, avaliação e preço.

**Query Parameters:**
- `nome` (string, optional): Nome do estabelecimento para busca
- `cidade` (string, optional): Cidade para filtrar estabelecimentos
- `bairro` (string, optional): Bairro para filtrar estabelecimentos
- `servico` (string, optional): Tipo de serviço oferecido
- `avaliacaoMinima` (number, optional): Avaliação mínima (1.0 a 5.0)
- `precoMaximo` (number, optional): Preço máximo dos serviços

**Exemplo:**
```
GET /api/estabelecimentos/buscar?nome=salao&cidade=São Paulo&avaliacaoMinima=4.0
```

**Responses:**
- `200 OK`: Estabelecimentos encontrados com base nos filtros

---

### POST /api/estabelecimentos/{id}/avaliacoes
**Avaliar estabelecimento**

Permite que um cliente avalie um estabelecimento com estrelas e comentários.

**Parameters:**
- `id` (path, required): ID do estabelecimento a ser avaliado
- `clienteId` (form, required): ID do cliente que está avaliando
- `estrelas` (form, required): Número de estrelas (1 a 5)
- `comentario` (form, required): Comentário sobre o estabelecimento

**Request Body (form-data):**
```
clienteId: b2fb343c-d27e-48cf-aecb-c3cec1e541f2
estrelas: 5
comentario: Excelente estabelecimento! Atendimento de primeira qualidade.
```

**Responses:**
- `200 OK`: Avaliação registrada com sucesso
- `400 Bad Request`: Dados de avaliação inválidos
- `404 Not Found`: Estabelecimento ou cliente não encontrado

---

## 2. Clientes

### POST /api/clientes
**Criar novo cliente**

Cadastra um novo cliente no sistema.

**Request Body:**
```json
{
  "nome": "Leticia MO",
  "email": "leticia@email.com",
  "telefone": "11999999999",
  "endereco": {
    "logradouro": "Rua A",
    "numero": "456",
    "complemento": "Apto 101",
    "bairro": "Vila B",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "12345678"
  }
}
```

**Responses:**
- `200 OK`: Cliente criado com sucesso
- `400 Bad Request`: Dados inválidos
- `409 Conflict`: Email já cadastrado

---

### GET /api/clientes
**Listar todos os clientes**

Retorna a lista completa de clientes cadastrados.

**Responses:**
- `200 OK`: Lista de clientes

---

### GET /api/clientes/{id}
**Buscar cliente por ID**

Retorna os detalhes de um cliente específico.

**Parameters:**
- `id` (path, required): ID único do cliente

**Responses:**
- `200 OK`: Cliente encontrado
- `404 Not Found`: Cliente não encontrado

---

### PUT /api/clientes/{id}
**Atualizar cliente**

Atualiza os dados de um cliente existente.

**Parameters:**
- `id` (path, required): ID do cliente a ser atualizado

**Request Body:**
```json
{
  "nome": "Joyce",
  "email": "joyce@email.com",
  "telefone": "11888888888",
  "endereco": {
    "logradouro": "Rua Nova",
    "numero": "789",
    "complemento": "Casa",
    "bairro": "Vila Nova",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "87654321"
  }
}
```

**Responses:**
- `200 OK`: Cliente atualizado com sucesso
- `404 Not Found`: Cliente não encontrado
- `400 Bad Request`: Dados inválidos

---

### DELETE /api/clientes/{id}
**Deletar cliente**

Remove um cliente do sistema.

**Parameters:**
- `id` (path, required): ID do cliente a ser removido

**Responses:**
- `204 No Content`: Cliente removido com sucesso
- `404 Not Found`: Cliente não encontrado

---

### GET /api/clientes/{id}/agendamentos
**Buscar agendamentos por cliente**

Retorna todos os agendamentos de um cliente específico.

**Parameters:**
- `id` (path, required): ID do cliente
- `status` (query, optional): Filtrar por status do agendamento

**Responses:**
- `200 OK`: Lista de agendamentos do cliente

---

### POST /api/clientes/{id}/calendario
**Integração com calendário do cliente**

Integra o calendário do cliente com calendários externos.

**Parameters:**
- `id` (path, required): ID do cliente
- `email` (query, required): Email do calendário a ser integrado

**Exemplo:**
```
POST /api/clientes/b2fb343c-d27e-48cf-aecb-c3cec1e541f2/calendario?email=leticia@gmail.com
```

**Responses:**
- `200 OK`: Integração realizada com sucesso
- `404 Not Found`: Cliente não encontrado

---

### DELETE /api/clientes/{id}/calendario
**Remover integração do calendário do cliente**

Remove a integração com calendário externo.

**Parameters:**
- `id` (path, required): ID do cliente

**Responses:**
- `204 No Content`: Integração removida com sucesso

---

## 3. Profissionais

### POST /api/profissionais
**Criar novo profissional**

Cadastra um novo profissional no sistema.

**Request Body:**
```json
{
  "nome": "Maria Santos",
  "especialidades": "Corte feminino, Coloração, Escova",
  "foto": "https://exemplo.com/maria.jpg",
  "tarifaBase": 80.0,
  "servicosIds": [
    "8ab95923-54ed-41eb-9db6-1af414160fe2"
  ],
  "disponibilidade": [
    {
      "diaSemana": "MONDAY",
      "horarioInicio": "09:00",
      "horarioFim": "17:00"
    },
    {
      "diaSemana": "TUESDAY",
      "horarioInicio": "09:00",
      "horarioFim": "17:00"
    }
  ]
}
```

**Responses:**
- `200 OK`: Profissional criado com sucesso
- `400 Bad Request`: Dados inválidos

---

### GET /api/profissionais
**Listar todos os profissionais**

Retorna a lista completa de profissionais cadastrados.

**Responses:**
- `200 OK`: Lista de profissionais

---

### GET /api/profissionais/{id}
**Buscar profissional por ID**

Retorna os detalhes de um profissional específico.

**Parameters:**
- `id` (path, required): ID único do profissional

**Responses:**
- `200 OK`: Profissional encontrado
- `404 Not Found`: Profissional não encontrado

---

### PUT /api/profissionais/{id}
**Atualizar profissional**

Atualiza os dados de um profissional existente.

**Parameters:**
- `id` (path, required): ID do profissional

**Responses:**
- `200 OK`: Profissional atualizado
- `404 Not Found`: Profissional não encontrado

---

### DELETE /api/profissionais/{id}
**Deletar profissional**

Remove um profissional do sistema.

**Parameters:**
- `id` (path, required): ID do profissional

**Responses:**
- `204 No Content`: Profissional removido
- `404 Not Found`: Profissional não encontrado

---

### POST /api/profissionais/{id}/servicos
**Adicionar serviço ao profissional**

Vincula um serviço a um profissional.

**Parameters:**
- `id` (path, required): ID do profissional
- `servicoId` (query, required): ID do serviço a ser vinculado

**Exemplo:**
```
POST /api/profissionais/441f94c5-c8fd-4cf5-96b2-66d624d03227/servicos?servicoId=f91a374c-bdb2-4499-825e-64f914aaa1ec
```

**Responses:**
- `200 OK`: Serviço vinculado com sucesso
- `404 Not Found`: Profissional ou serviço não encontrado

---

### DELETE /api/profissionais/{id}/servicos/{servicoId}
**Remover serviço do profissional**

Remove a vinculação de um serviço com o profissional.

**Parameters:**
- `id` (path, required): ID do profissional
- `servicoId` (path, required): ID do serviço

**Responses:**
- `204 No Content`: Vinculação removida
- `404 Not Found`: Profissional ou serviço não encontrado

---

### POST /api/profissionais/{id}/disponibilidade
**Adicionar disponibilidade do profissional**

Define horários de disponibilidade do profissional.

**Parameters:**
- `id` (path, required): ID do profissional

**Request Body:**
```json
{
  "dia": "MONDAY",
  "inicio": "09:00",
  "fim": "17:00"
}
```

**Responses:**
- `200 OK`: Disponibilidade definida
- `404 Not Found`: Profissional não encontrado

---

### POST /api/profissionais/{id}/avaliacoes
**Avaliar profissional**

Permite que um cliente avalie um profissional.

**Parameters:**
- `id` (path, required): ID do profissional
- `clienteId` (form, required): ID do cliente
- `estrelas` (form, required): Número de estrelas (1 a 5)
- `comentario` (form, required): Comentário sobre o profissional

**Request Body (form-data):**
```
clienteId: b2fb343c-d27e-48cf-aecb-c3cec1e541f2
estrelas: 5
comentario: Excelente profissional! Muito habilidoso e atencioso.
```

**Responses:**
- `200 OK`: Avaliação registrada
- `404 Not Found`: Profissional ou cliente não encontrado

---

### GET /api/profissionais/{id}/agendamentos
**Listar agendamentos do profissional**

Retorna os agendamentos de um profissional específico.

**Parameters:**
- `id` (path, required): ID do profissional
- `status` (query, optional): Filtrar por status

**Responses:**
- `200 OK`: Lista de agendamentos do profissional

---

## 4. Serviços

### POST /api/servicos
**Criar novo serviço**

Cadastra um novo serviço no sistema.

**Request Body:**
```json
{
  "nome": "Manicure Completa",
  "descricao": "Manicure com esmaltação e cuidados das unhas",
  "categoria": "Unhas",
  "preco": 35.00,
  "duracaoMinutos": 45
}
```

**Responses:**
- `200 OK`: Serviço criado com sucesso
- `400 Bad Request`: Dados inválidos

---

### GET /api/servicos
**Listar todos os serviços**

Retorna a lista completa de serviços disponíveis.

**Responses:**
- `200 OK`: Lista de serviços

---

### GET /api/servicos/{id}
**Buscar serviço por ID**

Retorna os detalhes de um serviço específico.

**Parameters:**
- `id` (path, required): ID único do serviço

**Responses:**
- `200 OK`: Serviço encontrado
- `404 Not Found`: Serviço não encontrado

---

### PUT /api/servicos/{id}
**Atualizar serviço**

Atualiza os dados de um serviço existente.

**Parameters:**
- `id` (path, required): ID do serviço

**Request Body:**
```json
{
  "nome": "Corte de Cabelo Premium",
  "descricao": "Corte moderno com lavagem e finalização",
  "categoria": "Cabelo",
  "preco": 65.00,
  "duracaoMinutos": 35
}
```

**Responses:**
- `200 OK`: Serviço atualizado
- `404 Not Found`: Serviço não encontrado

---

### DELETE /api/servicos/{id}
**Deletar serviço**

Remove um serviço do sistema.

**Parameters:**
- `id` (path, required): ID do serviço

**Responses:**
- `204 No Content`: Serviço removido
- `404 Not Found`: Serviço não encontrado

---

### GET /api/servicos/categorias
**Listar categorias de serviços**

Retorna todas as categorias de serviços disponíveis.

**Responses:**
- `200 OK`: Lista de categorias

---

### GET /api/servicos/estabelecimento/{estabelecimentoId}
**Buscar serviços por estabelecimento**

Retorna os serviços oferecidos por um estabelecimento específico.

**Parameters:**
- `estabelecimentoId` (path, required): ID do estabelecimento

**Responses:**
- `200 OK`: Lista de serviços do estabelecimento

---

### GET /api/servicos/profissional/{profissionalId}
**Buscar serviços por profissional**

Retorna os serviços oferecidos por um profissional específico.

**Parameters:**
- `profissionalId` (path, required): ID do profissional

**Responses:**
- `200 OK`: Lista de serviços do profissional

---

## 5. Agendamentos

### POST /api/agendamentos
**Criar novo agendamento**

Cria um novo agendamento no sistema com validações de disponibilidade e horário de funcionamento.

**Request Body:**
```json
{
  "clienteId": "827fae44-e8f5-4cf2-a244-07f34e461d00",
  "estabelecimentoId": "b6984ffd-adc1-4ede-af6f-955689b2120c",
  "profissionalId": "0920731c-2e9c-488d-a0f3-bea4783d4e32",
  "servicoId": "f91a374c-bdb2-4499-825e-64f914aaa1ec",
  "inicio": "2024-11-17T10:30:00-03:00"
}
```

**Responses:**
- `200 OK`: Agendamento criado com sucesso
- `400 Bad Request`: Dados inválidos ou horário indisponível
- `409 Conflict`: Conflito de agendamento - horário já ocupado
- `422 Unprocessable Entity`: Estabelecimento fechado neste horário

---

### GET /api/agendamentos
**Listar agendamentos**

Retorna a lista de todos os agendamentos.

**Responses:**
- `200 OK`: Lista de agendamentos

---

### GET /api/agendamentos/{id}
**Buscar agendamento por ID**

Retorna os detalhes de um agendamento específico.

**Parameters:**
- `id` (path, required): ID único do agendamento

**Responses:**
- `200 OK`: Agendamento encontrado
- `404 Not Found`: Agendamento não encontrado

---

### DELETE /api/agendamentos/{id}/cancelar
**Cancelar agendamento**

Cancela um agendamento existente e envia notificações automáticas.

**Parameters:**
- `id` (path, required): ID do agendamento a ser cancelado

**Responses:**
- `204 No Content`: Agendamento cancelado com sucesso
- `404 Not Found`: Agendamento não encontrado
- `422 Unprocessable Entity`: Agendamento não pode ser cancelado

---

### PATCH /api/agendamentos/{id}/confirmar
**Confirmar agendamento**

Confirma um agendamento pendente, mudando seu status para CONFIRMADO.

**Parameters:**
- `id` (path, required): ID do agendamento a ser confirmado

**Responses:**
- `204 No Content`: Agendamento confirmado com sucesso
- `404 Not Found`: Agendamento não encontrado
- `422 Unprocessable Entity`: Agendamento não pode ser confirmado

---

### PATCH /api/agendamentos/{id}/concluir
**Concluir agendamento**

Marca um agendamento como concluído após a realização do serviço.

**Parameters:**
- `id` (path, required): ID do agendamento a ser concluído

**Responses:**
- `204 No Content`: Agendamento concluído com sucesso
- `404 Not Found`: Agendamento não encontrado
- `422 Unprocessable Entity`: Agendamento não pode ser concluído

---

### PUT /api/agendamentos/{id}/reagendar
**Reagendar agendamento**

Altera a data e horário de um agendamento existente.

**Parameters:**
- `id` (path, required): ID do agendamento a ser reagendado

**Request Body:**
```json
{
  "clienteId": "b2fb343c-d27e-48cf-aecb-c3cec1e541f2",
  "estabelecimentoId": "b6984ffd-adc1-4ede-af6f-955689b2120c",
  "profissionalId": "441f94c5-c8fd-4cf5-96b2-66d624d03227",
  "servicoId": "f91a374c-bdb2-4499-825e-64f914aaa1ec",
  "inicio": "2024-12-20T16:00:00-03:00"
}
```

**Responses:**
- `204 No Content`: Agendamento reagendado com sucesso
- `404 Not Found`: Agendamento não encontrado
- `409 Conflict`: Novo horário conflita com outro agendamento
- `422 Unprocessable Entity`: Agendamento não pode ser reagendado

---

### GET /api/agendamentos/status/{status}
**Buscar agendamentos por status**

Retorna agendamentos filtrados por status.

**Parameters:**
- `status` (path, required): Status do agendamento (PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO)

**Responses:**
- `200 OK`: Lista de agendamentos com o status especificado

---

### GET /api/agendamentos/profissional/{profissionalId}
**Buscar agendamentos por profissional**

Retorna os agendamentos de um profissional específico.

**Parameters:**
- `profissionalId` (path, required): ID do profissional

**Responses:**
- `200 OK`: Lista de agendamentos do profissional

---

### GET /api/agendamentos/cliente/{clienteId}
**Buscar agendamentos por cliente**

Retorna os agendamentos de um cliente específico.

**Parameters:**
- `clienteId` (path, required): ID do cliente

**Responses:**
- `200 OK`: Lista de agendamentos do cliente

---

### GET /api/agendamentos/estabelecimento/{estabelecimentoId}
**Buscar agendamentos por estabelecimento**

Retorna os agendamentos de um estabelecimento específico.

**Parameters:**
- `estabelecimentoId` (path, required): ID do estabelecimento

**Responses:**
- `200 OK`: Lista de agendamentos do estabelecimento

---

## 6. Notificações

### GET /api/notificacoes/cliente/{clienteId}
**Conexão SSE para clientes**

Estabelece uma conexão Server-Sent Events para notificações em tempo real para clientes.

**Parameters:**
- `clienteId` (path, required): ID do cliente

**Content-Type:** `text/event-stream`

**Responses:**
- `200 OK`: Conexão SSE estabelecida

---

### GET /api/notificacoes/profissional/{profissionalId}
**Conexão SSE para profissionais**

Estabelece uma conexão Server-Sent Events para notificações em tempo real para profissionais.

**Parameters:**
- `profissionalId` (path, required): ID do profissional

**Content-Type:** `text/event-stream`

**Responses:**
- `200 OK`: Conexão SSE estabelecida

---

### GET /api/notificacoes/estabelecimento/{estabelecimentoId}
**Conexão SSE para estabelecimentos**

Estabelece uma conexão Server-Sent Events para notificações em tempo real para estabelecimentos.

**Parameters:**
- `estabelecimentoId` (path, required): ID do estabelecimento

**Content-Type:** `text/event-stream`

**Responses:**
- `200 OK`: Conexão SSE estabelecida

---

### POST /api/notificacoes/teste/{clienteId}
**Teste de notificações para clientes**

Envia uma notificação de teste para um cliente específico.

**Parameters:**
- `clienteId` (path, required): ID do cliente

**Responses:**
- `200 OK`: Notificação de teste enviada

---

### POST /api/notificacoes/teste/profissional/{profissionalId}
**Teste de notificações para profissionais**

Envia uma notificação de teste para um profissional específico.

**Parameters:**
- `profissionalId` (path, required): ID do profissional

**Responses:**
- `200 OK`: Notificação de teste enviada

---

### POST /api/notificacoes/teste/estabelecimento/{estabelecimentoId}
**Teste de notificações para estabelecimentos**

Envia uma notificação de teste para um estabelecimento específico.

**Parameters:**
- `estabelecimentoId` (path, required): ID do estabelecimento

**Responses:**
- `200 OK`: Notificação de teste enviada

---

## Status Codes

### Códigos de Sucesso
- `200 OK`: Requisição processada com sucesso
- `201 Created`: Recurso criado com sucesso
- `204 No Content`: Requisição processada sem conteúdo de retorno

### Códigos de Erro do Cliente
- `400 Bad Request`: Dados da requisição inválidos
- `401 Unauthorized`: Autenticação necessária
- `403 Forbidden`: Acesso negado
- `404 Not Found`: Recurso não encontrado
- `409 Conflict`: Conflito com estado atual do recurso
- `422 Unprocessable Entity`: Dados válidos mas regras de negócio impedem processamento

### Códigos de Erro do Servidor
- `500 Internal Server Error`: Erro interno do servidor
- `503 Service Unavailable`: Serviço temporariamente indisponível

---

## Exemplos de Uso

### Fluxo Completo de Agendamento

1. **Criar Cliente:**
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "11999999999",
    "endereco": {
      "logradouro": "Rua A",
      "numero": "123",
      "bairro": "Centro",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01234567"
    }
  }'
```

2. **Buscar Estabelecimentos:**
```bash
curl "http://localhost:8080/api/estabelecimentos/buscar?cidade=São Paulo&avaliacaoMinima=4.0"
```

3. **Criar Agendamento:**
```bash
curl -X POST http://localhost:8080/api/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "uuid-do-cliente",
    "estabelecimentoId": "uuid-do-estabelecimento",
    "profissionalId": "uuid-do-profissional",
    "servicoId": "uuid-do-servico",
    "inicio": "2024-11-17T10:30:00-03:00"
  }'
```

4. **Confirmar Agendamento:**
```bash
curl -X PATCH http://localhost:8080/api/agendamentos/uuid-do-agendamento/confirmar
```

---

## Recursos Adicionais

### Filtros de Busca Avançada
- **Por localização**: cidade, bairro, proximidade
- **Por serviços**: categoria, tipo, preço
- **Por avaliação**: estrelas mínimas, comentários
- **Por disponibilidade**: datas, horários
- **Por profissional**: especialidades, experiência

### Notificações Automáticas
- **Confirmação de agendamento**: enviada ao criar
- **Lembretes**: enviados 24h e 1h antes
- **Cancelamento**: notifica todas as partes
- **Reagendamento**: confirma nova data/hora
- **Avaliação**: solicita feedback após conclusão

### Integrações
- **Calendários externos**: Google Calendar, Outlook
- **Notificações push**: Para aplicativos móveis
- **Email**: Confirmações e lembretes
- **WhatsApp**: Notificações via API

---

## Configuração do Ambiente

### Variáveis de Ambiente
```properties
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agendamento
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=senha

# JWT
JWT_SECRET=sua-chave-secreta-jwt
JWT_EXPIRATION=86400000

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=seu-email@gmail.com
SPRING_MAIL_PASSWORD=sua-senha-aplicativo

# Servidor
SERVER_PORT=8080
```

### Headers Recomendados
```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer <jwt-token>
```

---

## Arquitetura e Tecnologias

### Stack Tecnológico
- **Java 17** + **Spring Boot 3.1**
- **Spring Security** para autenticação JWT
- **Spring Data JPA** para persistência
- **PostgreSQL** como banco de dados
- **Flyway** para migrações
- **SpringDoc OpenAPI** para documentação
- **Docker** para containerização

### Princípios Arquiteturais
- **Clean Architecture**: Separação clara entre camadas
- **Domain-Driven Design**: Modelagem baseada no domínio
- **SOLID**: Princípios de design de software
- **RESTful API**: Padrões REST para comunicação
- **Event-Driven**: Notificações baseadas em eventos

---

Este documento fornece uma visão completa da API do Sistema de Agendamento. Para mais detalhes, consulte a documentação interativa em `/swagger-ui.html` quando o sistema estiver em execução.
