package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Cliente;
import com.agendamento.dominio.entidades.Endereco;
import com.agendamento.dominio.repositorios.ClienteRepositorio;
import com.agendamento.infraestrutura.dto.ClienteDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarClienteUseCase {
    private final ClienteRepositorio clienteRepositorio;

    public CriarClienteUseCase(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Transactional
    public Cliente executar(ClienteDTO dto) {
        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        var cliente = Cliente.criar(
            dto.nome(),
            dto.email(),
            dto.telefone(),
            endereco
        );

        return clienteRepositorio.salvar(cliente);
    }
}
