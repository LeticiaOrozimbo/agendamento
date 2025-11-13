package com.agendamento.infraestrutura.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadeDTO(
    DayOfWeek dia,
    LocalTime inicio,
    LocalTime fim
) {}
