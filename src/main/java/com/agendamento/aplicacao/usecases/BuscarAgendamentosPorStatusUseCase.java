package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BuscarAgendamentosPorStatusUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;

    public BuscarAgendamentosPorStatusUseCase(AgendamentoRepositorio agendamentoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Agendamento> executar(Agendamento.Status status) {
        return agendamentoRepositorio.buscarPorStatus(status);
    }
}
