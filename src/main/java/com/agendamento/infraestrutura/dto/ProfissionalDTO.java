package com.agendamento.infraestrutura.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ProfissionalDTO(
    String nome,
    String especialidades,
    String foto,
    double tarifaBase,
    List<UUID> servicosIds,
    List<DisponibilidadeHorarioDTO> disponibilidade
) {}
