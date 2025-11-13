package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Servico;
import java.time.Duration;
import java.util.UUID;

public record ServicoResponseDTO(
    UUID id,
    String nome,
    String descricao,
    String categoria,
    double preco,
    Duration duracao
) {
    public ServicoResponseDTO(Servico servico) {
        this(
            servico.getId(),
            servico.getNome(),
            servico.getDescricao(),
            servico.getCategoria(),
            servico.getPreco(),
            servico.getDuracao()
        );
    }
}
