package com.agendamento.dominio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalTime;

@Embeddable
public class HorarioFuncionamento {
    @Column(name = "horario_abertura", nullable = false)
    private LocalTime abertura;

    @Column(name = "horario_fechamento", nullable = false)
    private LocalTime fechamento;

    protected HorarioFuncionamento() {
    }

    public HorarioFuncionamento(LocalTime abertura, LocalTime fechamento) {
        if (abertura == null || fechamento == null) {
            throw new IllegalArgumentException("Horários não podem ser nulos");
        }
        if (abertura.isAfter(fechamento)) {
            throw new IllegalArgumentException("Horário de abertura deve ser anterior ao de fechamento");
        }
        this.abertura = abertura;
        this.fechamento = fechamento;
    }

    public LocalTime getAbertura() {
        return abertura;
    }

    public LocalTime getFechamento() {
        return fechamento;
    }
}
