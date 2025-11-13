package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class BuscarAgendamentoPorIdUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;

    public BuscarAgendamentoPorIdUseCase(AgendamentoRepositorio agendamentoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional(readOnly = true)
    public Agendamento executar(UUID agendamentoId) {
        return agendamentoRepositorio.buscarPorId(agendamentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Agendamento não encontrado"));
    }
}
