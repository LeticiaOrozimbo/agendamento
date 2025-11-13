package com.agendamento.dominio.entidades;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
@Getter
public class Agendamento {
    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "estabelecimento_id", nullable = false)
    private UUID estabelecimentoId;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        AGENDADO,
        CONFIRMADO,
        CANCELADO,
        CONCLUIDO,
        NAO_COMPARECEU
    }

    protected Agendamento() {
        this.criadoEm = Instant.now();
    }

    private Agendamento(UUID id, UUID clienteId, UUID estabelecimentoId,
                       UUID profissionalId, UUID servicoId,
                       Instant inicio, Instant fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Início deve ser anterior ao fim");
        }

        this.id = id;
        this.clienteId = clienteId;
        this.estabelecimentoId = estabelecimentoId;
        this.profissionalId = profissionalId;
        this.servicoId = servicoId;
        this.inicio = inicio;
        this.fim = fim;
        this.criadoEm = Instant.now();
        this.status = Status.AGENDADO;
    }

    public static Agendamento criar(UUID clienteId, UUID estabelecimentoId,
                                  UUID profissionalId, UUID servicoId,
                                  Instant inicio, Instant fim) {
        return new Agendamento(UUID.randomUUID(), clienteId, estabelecimentoId,
                             profissionalId, servicoId, inicio, fim);
    }

    public void confirmar() {
        if (status != Status.AGENDADO) {
            throw new IllegalStateException("Agendamento não pode ser confirmado");
        }
        this.status = Status.CONFIRMADO;
    }

    public void cancelar() {
        if (status != Status.AGENDADO && status != Status.CONFIRMADO) {
            throw new IllegalStateException("Agendamento não pode ser cancelado");
        }
        this.status = Status.CANCELADO;
    }

    public void concluir() {
        if (status != Status.CONFIRMADO) {
            throw new IllegalStateException("Agendamento não pode ser concluído");
        }
        this.status = Status.CONCLUIDO;
    }

    public void marcarNaoCompareceu() {
        if (status != Status.CONFIRMADO) {
            throw new IllegalStateException("Não é possível marcar não comparecimento");
        }
        this.status = Status.NAO_COMPARECEU;
    }

    public void reagendar(Instant novoInicio, Instant novoFim) {
        if (status != Status.AGENDADO && status != Status.CONFIRMADO) {
            throw new IllegalStateException("Agendamento não pode ser reagendado");
        }
        if (novoInicio.isAfter(novoFim)) {
            throw new IllegalArgumentException("Novo início deve ser anterior ao novo fim");
        }
        this.inicio = novoInicio;
        this.fim = novoFim;
    }
}
