package com.agendamento.infraestrutura.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioFuncionamentoDTO(
    DayOfWeek diaSemana,
    LocalTime horarioAbertura,
    LocalTime horarioFechamento
) {}
