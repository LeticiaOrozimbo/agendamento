package com.agendamento.infraestrutura.dto;

import java.time.Instant;
import java.util.UUID;

public record AgendamentoDTO(
    UUID clienteId,
    UUID estabelecimentoId,
    UUID profissionalId,
    UUID servicoId,
    Instant inicio
) {}
