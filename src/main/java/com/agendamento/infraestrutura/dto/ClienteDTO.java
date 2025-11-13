package com.agendamento.infraestrutura.dto;

public record ClienteDTO(
    String nome,
    String email,
    String telefone,
    EnderecoDTO endereco
) {}
