package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Agendamento;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AgendamentoResponseDTO {
    private final UUID id;
    private final UUID clienteId;
    private final UUID profissionalId;
    private final UUID servicoId;
    private final UUID estabelecimentoId;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private final String status;

    public AgendamentoResponseDTO(Agendamento agendamento) {
        this.id = agendamento.getId();
        this.clienteId = agendamento.getClienteId();
        this.profissionalId = agendamento.getProfissionalId();
        this.servicoId = agendamento.getServicoId();
        this.estabelecimentoId = agendamento.getEstabelecimentoId();
        this.inicio = LocalDateTime.ofInstant(agendamento.getInicio(), java.time.ZoneId.systemDefault());
        this.fim = LocalDateTime.ofInstant(agendamento.getFim(), java.time.ZoneId.systemDefault());
        this.status = agendamento.getStatus().name();
    }
}
