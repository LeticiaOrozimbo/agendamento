package com.agendamento.infraestrutura.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadeHorarioDTO(
    DayOfWeek diaSemana,
    LocalTime horarioInicio,
    LocalTime horarioFim
) {}
