package com.agendamento.infraestrutura.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadeHorarioResponseDTO(
    DayOfWeek diaSemana,
    LocalTime inicio,
    LocalTime fim
) {}
