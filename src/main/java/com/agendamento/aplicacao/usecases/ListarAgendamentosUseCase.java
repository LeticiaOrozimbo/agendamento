package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class ListarAgendamentosUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;

    public ListarAgendamentosUseCase(AgendamentoRepositorio agendamentoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porProfissional(UUID profissionalId) {
        return agendamentoRepositorio.buscarPorProfissional(profissionalId);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porCliente(UUID clienteId) {
        return agendamentoRepositorio.buscarPorCliente(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porEstabelecimento(UUID estabelecimentoId) {
        return agendamentoRepositorio.buscarPorEstabelecimento(estabelecimentoId);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> todos() {
        return agendamentoRepositorio.buscarTodos();
    }
}
