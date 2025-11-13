package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Endereco;

public record EnderecoResponseDTO(
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep
) {
    public EnderecoResponseDTO(Endereco endereco) {
        this(
            endereco.getLogradouro(),
            endereco.getNumero(),
            endereco.getComplemento(),
            endereco.getBairro(),
            endereco.getCidade(),
            endereco.getEstado(),
            endereco.getCep()
        );
    }
}
