package com.agendamento.aplicacao.usecases;


import com.agendamento.aplicacao.CalendarioService;
import com.agendamento.aplicacao.INotificacaoService;
import com.agendamento.aplicacao.NotificacaoPushService;
import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.dominio.entidades.*;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import com.agendamento.infraestrutura.dto.AgendamentoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarAgendamentoUseCase - Testes unitários")
class CriarAgendamentoUseCaseTest {

    @Mock
    private AgendamentoRepositorio agendamentoRepositorio;

    @Mock
    private CatalogoService catalogoService;

    @Mock
    private INotificacaoService notificacaoService;

    @Mock
    private NotificacaoPushService notificacaoPushService;

    @Mock
    private CalendarioService calendarioService;

    private CriarAgendamentoUseCase criarAgendamentoUseCase;

    @BeforeEach
    void setUp() {
        criarAgendamentoUseCase = new CriarAgendamentoUseCase(
                agendamentoRepositorio,
                catalogoService,
                notificacaoService,
                notificacaoPushService,
                calendarioService
        );
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() {
        // Given
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, inicio);

        Servico servico = Servico.criar("Corte", "Corte de cabelo", "Cabelo", 50.0, 60);
        Cliente cliente = criarClienteMock();

        when(catalogoService.obterServico(servicoId)).thenReturn(servico);
        when(catalogoService.obterCliente(clienteId)).thenReturn(cliente);
        when(catalogoService.obterEstabelecimento(estabelecimentoId)).thenReturn(criarEstabelecimentoMock());
        when(catalogoService.obterProfissional(profissionalId)).thenReturn(criarProfissionalMock());
        when(agendamentoRepositorio.buscarConflitosParaProfissional(eq(profissionalId), any(), any()))
                .thenReturn(List.of());
        when(agendamentoRepositorio.salvar(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Agendamento resultado = criarAgendamentoUseCase.executar(dto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getClienteId()).isEqualTo(clienteId);
        assertThat(resultado.getEstabelecimentoId()).isEqualTo(estabelecimentoId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(resultado.getServicoId()).isEqualTo(servicoId);
        assertThat(resultado.getInicio()).isEqualTo(inicio);
        assertThat(resultado.getFim()).isEqualTo(inicio.plus(Duration.ofMinutes(60)));
        assertThat(resultado.getStatus()).isEqualTo(Agendamento.Status.AGENDADO);

        verify(agendamentoRepositorio).salvar(any(Agendamento.class));
        verify(notificacaoService).agendarLembretes(any(Agendamento.class));
        verify(notificacaoPushService).notificarNovoAgendamento(any(Agendamento.class));
        verify(calendarioService).adicionarEvento(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe")
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        // Given
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, inicio);

        when(catalogoService.obterCliente(clienteId))
                .thenThrow(new RegraNegocioExcecao("Cliente não encontrado"));

        // When & Then
        assertThatThrownBy(() -> criarAgendamentoUseCase.executar(dto))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Cliente não encontrado");

        verify(agendamentoRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando há conflito de horário")
    void deveLancarExcecaoQuandoHaConflitoDeHorario() {
        // Given
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);

        AgendamentoDTO dto = new AgendamentoDTO(clienteId, estabelecimentoId, profissionalId, servicoId, inicio);

        Servico servico = Servico.criar("Corte", "Corte de cabelo", "Cabelo", 50.0, 60);
        Cliente cliente = criarClienteMock();

        Agendamento agendamentoExistente = Agendamento.criar(
                UUID.randomUUID(), estabelecimentoId, profissionalId, servicoId,
                inicio, inicio.plus(1, ChronoUnit.HOURS)
        );

        when(catalogoService.obterServico(servicoId)).thenReturn(servico);
        when(catalogoService.obterCliente(clienteId)).thenReturn(cliente);
        when(catalogoService.obterEstabelecimento(estabelecimentoId)).thenReturn(criarEstabelecimentoMock());
        when(catalogoService.obterProfissional(profissionalId)).thenReturn(criarProfissionalMock());
        when(agendamentoRepositorio.buscarConflitosParaProfissional(eq(profissionalId), any(), any()))
                .thenReturn(List.of(agendamentoExistente));

        // When & Then
        assertThatThrownBy(() -> criarAgendamentoUseCase.executar(dto))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Conflito de agendamento para o profissional");

        verify(agendamentoRepositorio, never()).salvar(any());
    }

    private Cliente criarClienteMock() {
        Endereco endereco = Endereco.criar("Rua B", "456", null, "Centro", "São Paulo", "SP", "01234567");
        return Cliente.criar(
                "João Silva",
                "joao@email.com",
                "+5511999999999",
                endereco
        );
    }

    private Estabelecimento criarEstabelecimentoMock() {
        Endereco endereco = Endereco.criar("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567");
        return Estabelecimento.criar("Estabelecimento Test", endereco);
    }

    private Profissional criarProfissionalMock() {
        return Profissional.criar("Profissional Test");
    }
}
