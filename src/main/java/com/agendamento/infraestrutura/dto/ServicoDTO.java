package com.agendamento.infraestrutura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ServicoDTO(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    String descricao,

    @NotBlank(message = "Categoria é obrigatória")
    String categoria,

    @Positive(message = "Preço deve ser positivo")
    double preco,

    @Positive(message = "Duração em minutos deve ser positiva")
    int duracaoMinutos
) {}
