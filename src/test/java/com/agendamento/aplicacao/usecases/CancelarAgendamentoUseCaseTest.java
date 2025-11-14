package com.agendamento.aplicacao.usecases;

import com.agendamento.aplicacao.CalendarioService;
import com.agendamento.aplicacao.INotificacaoService;
import com.agendamento.aplicacao.NotificacaoPushService;
import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelarAgendamentoUseCase - Testes unitários")
class CancelarAgendamentoUseCaseTest {

    @Mock
    private AgendamentoRepositorio agendamentoRepositorio;

    @Mock
    private INotificacaoService notificacaoService;

    @Mock
    private NotificacaoPushService notificacaoPushService;

    @Mock
    private CalendarioService calendarioService;

    private CancelarAgendamentoUseCase cancelarAgendamentoUseCase;

    @BeforeEach
    void setUp() {
        cancelarAgendamentoUseCase = new CancelarAgendamentoUseCase(
                agendamentoRepositorio,
                notificacaoService,
                notificacaoPushService,
                calendarioService
        );
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void deveCancelarAgendamentoComSucesso() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoValido();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepositorio.salvar(any(Agendamento.class))).thenReturn(agendamento);

        cancelarAgendamentoUseCase.executar(agendamentoId);

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CANCELADO);

        verify(agendamentoRepositorio).salvar(agendamento);
        verify(calendarioService).removerEvento(agendamento);
        verify(notificacaoService).cancelarLembretes(any(UUID.class));
        verify(notificacaoPushService).notificarCancelamento(agendamento);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não existe")
    void deveLancarExcecaoQuandoAgendamentoNaoExiste() {
        UUID agendamentoId = UUID.randomUUID();
        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cancelarAgendamentoUseCase.executar(agendamentoId))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Agendamento não encontrado");

        verify(agendamentoRepositorio, never()).salvar(any());
        verify(calendarioService, never()).removerEvento(any());
        verify(notificacaoService, never()).cancelarLembretes(any());
        verify(notificacaoPushService, never()).notificarCancelamento(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento já está cancelado")
    void deveLancarExcecaoQuandoAgendamentoJaCancelado() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.cancelar();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> cancelarAgendamentoUseCase.executar(agendamentoId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser cancelado");

        verify(agendamentoRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve continuar execução mesmo com erro no calendário")
    void deveContinuarExecucaoMesmoComErroNoCalendario() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoValido();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepositorio.salvar(any(Agendamento.class))).thenReturn(agendamento);
        doThrow(new RuntimeException("Erro no calendário")).when(calendarioService).removerEvento(agendamento);

        assertThatCode(() -> cancelarAgendamentoUseCase.executar(agendamentoId))
                .doesNotThrowAnyException();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CANCELADO);
        verify(agendamentoRepositorio).salvar(agendamento);
        verify(calendarioService).removerEvento(agendamento);

        verify(notificacaoService, never()).cancelarLembretes(any(UUID.class));
        verify(notificacaoPushService, never()).notificarCancelamento(agendamento);
    }

    private Agendamento criarAgendamentoValido() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant fim = inicio.plus(1, ChronoUnit.HOURS);

        return Agendamento.criar(clienteId, estabelecimentoId, profissionalId, servicoId, inicio, fim);
    }
}
