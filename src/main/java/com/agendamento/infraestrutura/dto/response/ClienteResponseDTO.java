package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Cliente;
import java.util.UUID;

public record ClienteResponseDTO(
    UUID id,
    String nome,
    String email,
    String telefone,
    EnderecoResponseDTO endereco,
    boolean calendarioIntegrado,
    String emailCalendario
) {
    public ClienteResponseDTO(Cliente cliente) {
        this(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            new EnderecoResponseDTO(cliente.getEndereco()),
            cliente.getCalendarioIntegrado(),
            cliente.getEmailCalendario()
        );
    }
}
