package com.agendamento.infraestrutura.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioFuncionamentoResponseDTO(
    DayOfWeek diaSemana,
    LocalTime abertura,
    LocalTime fechamento
) {}
