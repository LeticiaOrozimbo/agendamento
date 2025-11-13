package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Estabelecimento;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record EstabelecimentoResponseDTO(
    UUID id,
    String nome,
    EnderecoResponseDTO endereco,
    String descricao,
    List<String> fotos,
    List<HorarioFuncionamentoResponseDTO> horariosFuncionamento,
    List<UUID> servicosIds,
    List<UUID> profissionaisIds,
    double avaliacaoMedia,
    List<AvaliacaoResponseDTO> avaliacoes
) {
    public EstabelecimentoResponseDTO(Estabelecimento estabelecimento) {
        this(
            estabelecimento.getId(),
            estabelecimento.getNome(),
            new EnderecoResponseDTO(estabelecimento.getEndereco()),
            estabelecimento.getDescricao(),
            estabelecimento.getFotos(),
            estabelecimento.getHorariosFuncionamento().entrySet().stream()
                .map(entry -> new HorarioFuncionamentoResponseDTO(
                    entry.getKey(),
                    entry.getValue().getAbertura(),
                    entry.getValue().getFechamento()))
                .collect(Collectors.toList()),
            estabelecimento.getServicosIds().stream().toList(),
            estabelecimento.getProfissionaisIds().stream().toList(),
            estabelecimento.getAvaliacaoMedia() != null ? estabelecimento.getAvaliacaoMedia().doubleValue() : 0.0,
            estabelecimento.getAvaliacoes().stream()
                .map(AvaliacaoResponseDTO::new)
                .collect(Collectors.toList())
        );
    }
}
