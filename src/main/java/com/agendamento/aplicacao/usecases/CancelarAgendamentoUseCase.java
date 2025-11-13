package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import com.agendamento.aplicacao.INotificacaoService;
import com.agendamento.aplicacao.NotificacaoPushService;
import com.agendamento.aplicacao.CalendarioService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CancelarAgendamentoUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;
    private final INotificacaoService notificacaoService;
    private final NotificacaoPushService notificacaoPushService;
    private final CalendarioService calendarioService;

    public CancelarAgendamentoUseCase(AgendamentoRepositorio agendamentoRepositorio,
                                    INotificacaoService notificacaoService,
                                    NotificacaoPushService notificacaoPushService,
                                    CalendarioService calendarioService) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.notificacaoService = notificacaoService;
        this.notificacaoPushService = notificacaoPushService;
        this.calendarioService = calendarioService;
    }

    @Transactional
    public void executar(UUID agendamentoId) {
        var agendamento = agendamentoRepositorio.buscarPorId(agendamentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Agendamento não encontrado"));

        // Aplicar regra de negócio através da entidade
        agendamento.cancelar();

        // Persistir alteração
        agendamentoRepositorio.salvar(agendamento);

        try {
            // Remover do calendário
            calendarioService.removerEvento(agendamento);

            // Cancelar lembretes
            notificacaoService.cancelarLembretes(agendamento.getId());

            // Enviar notificação push
            notificacaoPushService.notificarCancelamento(agendamento);
        } catch (Exception e) {
            System.err.println("Erro ao processar cancelamento: " + e.getMessage());
        }
    }
}
