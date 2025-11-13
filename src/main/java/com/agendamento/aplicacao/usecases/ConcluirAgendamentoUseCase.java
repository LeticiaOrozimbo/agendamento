package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ConcluirAgendamentoUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;

    public ConcluirAgendamentoUseCase(AgendamentoRepositorio agendamentoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional
    public void executar(UUID agendamentoId) {
        var agendamento = agendamentoRepositorio.buscarPorId(agendamentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Agendamento não encontrado"));

        // Aplicar regra de negócio através da entidade
        agendamento.concluir();

        // Persistir alteração
        agendamentoRepositorio.salvar(agendamento);
    }
}
