package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import com.agendamento.aplicacao.INotificacaoService;
import com.agendamento.aplicacao.NotificacaoPushService;
import com.agendamento.aplicacao.CalendarioService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReagendarAgendamentoUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;
    private final INotificacaoService notificacaoService;
    private final NotificacaoPushService notificacaoPushService;
    private final CalendarioService calendarioService;

    public ReagendarAgendamentoUseCase(AgendamentoRepositorio agendamentoRepositorio,
                                     INotificacaoService notificacaoService,
                                     NotificacaoPushService notificacaoPushService,
                                     CalendarioService calendarioService) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.notificacaoService = notificacaoService;
        this.notificacaoPushService = notificacaoPushService;
        this.calendarioService = calendarioService;
    }

    @Transactional
    public void executar(UUID agendamentoId, Instant novoInicio, Instant novoFim) {
        var agendamento = agendamentoRepositorio.buscarPorId(agendamentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Agendamento não encontrado"));

        // Validar conflitos no novo horário
        var conflitos = agendamentoRepositorio.buscarConflitosParaProfissional(
            agendamento.getProfissionalId(), novoInicio, novoFim
        );

        // Remover o próprio agendamento da lista de conflitos
        conflitos.removeIf(c -> c.getId().equals(agendamentoId));

        if (!conflitos.isEmpty()) {
            throw new RegraNegocioExcecao("Conflito de agendamento no novo horário");
        }

        // Aplicar regra de negócio através da entidade
        agendamento.reagendar(novoInicio, novoFim);

        // Persistir alteração
        agendamentoRepositorio.salvar(agendamento);

        try {
            // Atualizar calendário
            calendarioService.atualizarEvento(agendamento);

            // Agendar novos lembretes
            notificacaoService.agendarLembretes(agendamento);

            // Enviar notificação push
            notificacaoPushService.notificarReagendamento(agendamento);
        } catch (Exception e) {
            System.err.println("Erro ao processar reagendamento: " + e.getMessage());
        }
    }
}
