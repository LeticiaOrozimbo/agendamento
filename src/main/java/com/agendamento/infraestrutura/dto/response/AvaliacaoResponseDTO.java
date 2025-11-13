package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Avaliacao;
import java.util.Date;
import java.util.UUID;

public record AvaliacaoResponseDTO(
    UUID clienteId,
    int estrelas,
    String comentario,
    Date data
) {
    public AvaliacaoResponseDTO(Avaliacao avaliacao) {
        this(
            avaliacao.getClienteId(),
            avaliacao.getEstrelas(),
            avaliacao.getComentario(),
            Date.from(avaliacao.getData())
        );
    }
}
