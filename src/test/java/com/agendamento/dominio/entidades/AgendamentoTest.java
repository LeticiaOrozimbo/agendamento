package com.agendamento.dominio.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Agendamento - Testes unitários")
class AgendamentoTest {

    @Test
    @DisplayName("Deve criar agendamento válido")
    void deveCriarAgendamentoValido() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant fim = inicio.plus(1, ChronoUnit.HOURS);

        Agendamento agendamento = Agendamento.criar(clienteId, estabelecimentoId, profissionalId, servicoId, inicio, fim);

        assertThat(agendamento).isNotNull();
        assertThat(agendamento.getId()).isNotNull();
        assertThat(agendamento.getClienteId()).isEqualTo(clienteId);
        assertThat(agendamento.getEstabelecimentoId()).isEqualTo(estabelecimentoId);
        assertThat(agendamento.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(agendamento.getServicoId()).isEqualTo(servicoId);
        assertThat(agendamento.getInicio()).isEqualTo(inicio);
        assertThat(agendamento.getFim()).isEqualTo(fim);
        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.AGENDADO);
        assertThat(agendamento.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Não deve criar agendamento com início após o fim")
    void naoDeveCriarAgendamentoComInicioAposFim() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);
        Instant fim = inicio.minus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> Agendamento.criar(clienteId, estabelecimentoId, profissionalId, servicoId, inicio, fim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Início deve ser anterior ao fim");
    }

    @Test
    @DisplayName("Deve confirmar agendamento quando status é AGENDADO")
    void deveConfirmarAgendamentoQuandoStatusAgendado() {
        Agendamento agendamento = criarAgendamentoValido();

        agendamento.confirmar();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CONFIRMADO);
    }

    @Test
    @DisplayName("Não deve confirmar agendamento quando status não é AGENDADO")
    void naoDeveConfirmarAgendamentoQuandoStatusNaoAgendado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.cancelar();

        assertThatThrownBy(agendamento::confirmar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser confirmado");
    }

    @Test
    @DisplayName("Deve cancelar agendamento quando status é AGENDADO")
    void deveCancelarAgendamentoQuandoStatusAgendado() {
        Agendamento agendamento = criarAgendamentoValido();

        agendamento.cancelar();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CANCELADO);
    }

    @Test
    @DisplayName("Deve cancelar agendamento quando status é CONFIRMADO")
    void deveCancelarAgendamentoQuandoStatusConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.confirmar();

        agendamento.cancelar();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CANCELADO);
    }

    @Test
    @DisplayName("Não deve cancelar agendamento quando já está cancelado")
    void naoDeveCancelarAgendamentoQuandoJaCancelado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.cancelar();

        assertThatThrownBy(agendamento::cancelar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser cancelado");
    }

    @Test
    @DisplayName("Deve concluir agendamento quando status é CONFIRMADO")
    void deveConcluirAgendamentoQuandoStatusConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.confirmar();

        agendamento.concluir();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.CONCLUIDO);
    }

    @Test
    @DisplayName("Não deve concluir agendamento quando status não é CONFIRMADO")
    void naoDeveConcluirAgendamentoQuandoStatusNaoConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();

        assertThatThrownBy(agendamento::concluir)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser concluído");
    }

    @Test
    @DisplayName("Deve marcar não compareceu quando status é CONFIRMADO")
    void deveMarcarNaoCompareceuQuandoStatusConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.confirmar();

        agendamento.marcarNaoCompareceu();

        assertThat(agendamento.getStatus()).isEqualTo(Agendamento.Status.NAO_COMPARECEU);
    }

    @Test
    @DisplayName("Não deve marcar não compareceu quando status não é CONFIRMADO")
    void naoDeveMarcarNaoCompareceuQuandoStatusNaoConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();

        assertThatThrownBy(agendamento::marcarNaoCompareceu)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível marcar não comparecimento");
    }

    @Test
    @DisplayName("Deve reagendar agendamento quando status é AGENDADO")
    void deveReagendarAgendamentoQuandoStatusAgendado() {
        Agendamento agendamento = criarAgendamentoValido();
        Instant novoInicio = Instant.now().plus(3, ChronoUnit.HOURS);
        Instant novoFim = novoInicio.plus(1, ChronoUnit.HOURS);

        agendamento.reagendar(novoInicio, novoFim);

        assertThat(agendamento.getInicio()).isEqualTo(novoInicio);
        assertThat(agendamento.getFim()).isEqualTo(novoFim);
    }

    @Test
    @DisplayName("Deve reagendar agendamento quando status é CONFIRMADO")
    void deveReagendarAgendamentoQuandoStatusConfirmado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.confirmar();
        Instant novoInicio = Instant.now().plus(3, ChronoUnit.HOURS);
        Instant novoFim = novoInicio.plus(1, ChronoUnit.HOURS);

        agendamento.reagendar(novoInicio, novoFim);

        assertThat(agendamento.getInicio()).isEqualTo(novoInicio);
        assertThat(agendamento.getFim()).isEqualTo(novoFim);
    }

    @Test
    @DisplayName("Não deve reagendar agendamento quando cancelado")
    void naoDeveReagendarAgendamentoQuandoCancelado() {
        Agendamento agendamento = criarAgendamentoValido();
        agendamento.cancelar();
        Instant novoInicio = Instant.now().plus(3, ChronoUnit.HOURS);
        Instant novoFim = novoInicio.plus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> agendamento.reagendar(novoInicio, novoFim))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Agendamento não pode ser reagendado");
    }

    @Test
    @DisplayName("Não deve reagendar com novo início após novo fim")
    void naoDeveReagendarComNovoInicioAposNovoFim() {
        Agendamento agendamento = criarAgendamentoValido();
        Instant novoInicio = Instant.now().plus(4, ChronoUnit.HOURS);
        Instant novoFim = novoInicio.minus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> agendamento.reagendar(novoInicio, novoFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Novo início deve ser anterior ao novo fim");
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
