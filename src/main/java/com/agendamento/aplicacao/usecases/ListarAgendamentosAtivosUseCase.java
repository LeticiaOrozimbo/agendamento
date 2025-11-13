package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class ListarAgendamentosAtivosUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;

    public ListarAgendamentosAtivosUseCase(AgendamentoRepositorio agendamentoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Agendamento> todos() {
        return agendamentoRepositorio.buscarTodos().stream()
            .filter(a -> a.getStatus() != Agendamento.Status.CANCELADO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porProfissional(UUID profissionalId) {
        return agendamentoRepositorio.buscarPorProfissional(profissionalId).stream()
            .filter(a -> a.getStatus() != Agendamento.Status.CANCELADO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porCliente(UUID clienteId) {
        return agendamentoRepositorio.buscarPorCliente(clienteId).stream()
            .filter(a -> a.getStatus() != Agendamento.Status.CANCELADO)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Agendamento> porEstabelecimento(UUID estabelecimentoId) {
        return agendamentoRepositorio.buscarPorEstabelecimento(estabelecimentoId).stream()
            .filter(a -> a.getStatus() != Agendamento.Status.CANCELADO)
            .toList();
    }
}
