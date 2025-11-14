package com.agendamento.api;

import com.agendamento.aplicacao.usecases.*;
import com.agendamento.config.GlobalExceptionHandler;
import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.infraestrutura.dto.AgendamentoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AgendamentoController.class, GlobalExceptionHandler.class})
@DisplayName("AgendamentoController - Testes de integração")
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CriarAgendamentoUseCase criarAgendamentoUseCase;

    @MockBean
    private BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase;

    @MockBean
    private CancelarAgendamentoUseCase cancelarAgendamentoUseCase;

    @MockBean
    private ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase;

    @MockBean
    private ConcluirAgendamentoUseCase concluirAgendamentoUseCase;

    @MockBean
    private ReagendarAgendamentoUseCase reagendarAgendamentoUseCase;

    @MockBean
    private BuscarAgendamentosPorStatusUseCase buscarAgendamentosPorStatusUseCase;

    @MockBean
    private ListarAgendamentosAtivosUseCase listarAgendamentosAtivosUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() throws Exception {

        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, inicio);
        Agendamento agendamento = criarAgendamentoMock();

        when(criarAgendamentoUseCase.executar(any(AgendamentoDTO.class))).thenReturn(agendamento);

        mockMvc.perform(post("/api/agendamentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").exists())
                .andExpect(jsonPath("$.status").value("AGENDADO"));

        verify(criarAgendamentoUseCase).executar(any(AgendamentoDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 quando dados de agendamento inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(1, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, inicio);

        when(criarAgendamentoUseCase.executar(any(AgendamentoDTO.class)))
                .thenThrow(new RegraNegocioExcecao("Cliente não encontrado"));

        mockMvc.perform(post("/api/agendamentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve listar agendamentos ativos")
    void deveListarAgendamentosAtivos() throws Exception {
        List<Agendamento> agendamentos = Arrays.asList(
                criarAgendamentoMock(),
                criarAgendamentoMock()
        );
        when(listarAgendamentosAtivosUseCase.todos()).thenReturn(agendamentos);

        mockMvc.perform(get("/api/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(listarAgendamentosAtivosUseCase).todos();
    }

    @Test
    @WithMockUser
    @DisplayName("Deve obter agendamento por ID")
    void deveObterAgendamentoPorId() throws Exception {
        UUID id = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoMock();
        when(buscarAgendamentoPorIdUseCase.executar(id)).thenReturn(agendamento);

        mockMvc.perform(get("/api/agendamentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGENDADO"));

        verify(buscarAgendamentoPorIdUseCase).executar(id);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 quando agendamento não existe")
    void deveRetornar404QuandoAgendamentoNaoExiste() throws Exception {
        UUID id = UUID.randomUUID();
        when(buscarAgendamentoPorIdUseCase.executar(id))
                .thenThrow(new RegraNegocioExcecao("Agendamento não encontrado"));

        mockMvc.perform(get("/api/agendamentos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve cancelar agendamento")
    void deveCancelarAgendamento() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(cancelarAgendamentoUseCase).executar(id);

        mockMvc.perform(delete("/api/agendamentos/{id}/cancelar", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cancelarAgendamentoUseCase).executar(id);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar agendamentos por cliente")
    void deveBuscarAgendamentosPorCliente() throws Exception {
        UUID clienteId = UUID.randomUUID();
        List<Agendamento> agendamentos = List.of(criarAgendamentoMock());
        when(listarAgendamentosAtivosUseCase.porCliente(clienteId)).thenReturn(agendamentos);

        mockMvc.perform(get("/api/agendamentos/cliente/{clienteId}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(listarAgendamentosAtivosUseCase).porCliente(clienteId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar agendamentos por profissional")
    void deveBuscarAgendamentosPorProfissional() throws Exception {
        UUID profissionalId = UUID.randomUUID();
        List<Agendamento> agendamentos = List.of(criarAgendamentoMock());
        when(listarAgendamentosAtivosUseCase.porProfissional(profissionalId)).thenReturn(agendamentos);

        mockMvc.perform(get("/api/agendamentos/profissional/{profissionalId}", profissionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(listarAgendamentosAtivosUseCase).porProfissional(profissionalId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve buscar agendamentos por estabelecimento")
    void deveBuscarAgendamentosPorEstabelecimento() throws Exception {
        UUID estabelecimentoId = UUID.randomUUID();
        List<Agendamento> agendamentos = List.of(criarAgendamentoMock());
        when(listarAgendamentosAtivosUseCase.porEstabelecimento(estabelecimentoId)).thenReturn(agendamentos);

        mockMvc.perform(get("/api/agendamentos/estabelecimento/{estabelecimentoId}", estabelecimentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(listarAgendamentosAtivosUseCase).porEstabelecimento(estabelecimentoId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve reagendar agendamento")
    void deveReagendarAgendamento() throws Exception {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant novoInicio = Instant.now().plus(3, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, novoInicio);
        doNothing().when(reagendarAgendamentoUseCase).executar(eq(id), eq(novoInicio), any(Instant.class));

        mockMvc.perform(put("/api/agendamentos/{id}/reagendar", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(reagendarAgendamentoUseCase).executar(eq(id), eq(novoInicio), any(Instant.class));
    }

    private Agendamento criarAgendamentoMock() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant fim = inicio.plus(1, ChronoUnit.HOURS);

        return Agendamento.criar(clienteId, estabelecimentoId, profissionalId, servicoId, inicio, fim);
    }
}
