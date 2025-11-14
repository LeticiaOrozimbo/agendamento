package com.agendamento.aplicacao.usecases;

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
@DisplayName("ConcluirAgendamentoUseCase - Testes unitários")
class ConcluirAgendamentoUseCaseTest {

    @Mock
    private AgendamentoRepositorio agendamentoRepositorio;

    private ConcluirAgendamentoUseCase concluirAgendamentoUseCase;

    @BeforeEach
    void setUp() {
        concluirAgendamentoUseCase = new ConcluirAgendamentoUseCase(agendamentoRepositorio);
    }

    @Test
    @DisplayName("Deve concluir agendamento com sucesso")
    void deveConcluirAgendamentoComSucesso() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoConfirmado();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepositorio.salvar(any(Agendamento.class))).thenReturn(agendamento);

        concluirAgendamentoUseCase.executar(agendamentoId);

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CONCLUIDO);

        verify(agendamentoRepositorio).salvar(agendamento);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não existe")
    void deveLancarExcecaoQuandoAgendamentoNaoExiste() {
        UUID agendamentoId = UUID.randomUUID();
        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> concluirAgendamentoUseCase.executar(agendamentoId))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Agendamento não encontrado");

        verify(agendamentoRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não está confirmado")
    void deveLancarExcecaoQuandoAgendamentoNaoEstaConfirmado() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoValido();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> concluirAgendamentoUseCase.executar(agendamentoId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser concluído");

        verify(agendamentoRepositorio, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento já foi cancelado")
    void deveLancarExcecaoQuandoAgendamentoJaFoiCancelado() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.cancelar();

        when(agendamentoRepositorio.buscarPorId(agendamentoId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> concluirAgendamentoUseCase.executar(agendamentoId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser concluído");

        verify(agendamentoRepositorio, never()).salvar(any());
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

    private Agendamento criarAgendamentoConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.confirmar();
        return agendamento;
    }
}
