package com.agendamento.infraestrutura.dto;

import java.util.List;

public record EstabelecimentoDTO(
    String nome,
    EnderecoDTO endereco,
    String descricao,
    List<String> fotos,
    List<HorarioFuncionamentoDTO> horariosFuncionamento
) {}
