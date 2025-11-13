package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import com.agendamento.aplicacao.NotificacaoPushService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ConfirmarAgendamentoUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;
    private final NotificacaoPushService notificacaoPushService;

    public ConfirmarAgendamentoUseCase(AgendamentoRepositorio agendamentoRepositorio,
                                     NotificacaoPushService notificacaoPushService) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.notificacaoPushService = notificacaoPushService;
    }

    @Transactional
    public void executar(UUID agendamentoId) {
        var agendamento = agendamentoRepositorio.buscarPorId(agendamentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Agendamento não encontrado"));

        // Aplicar regra de negócio através da entidade
        agendamento.confirmar();

        // Persistir alteração
        agendamentoRepositorio.salvar(agendamento);

        // Enviar notificação push
        try {
            notificacaoPushService.notificarCliente(agendamento.getClienteId(),
                "Seu agendamento foi confirmado!");
            notificacaoPushService.notificarProfissional(agendamento.getProfissionalId(),
                "Agendamento confirmado!");
            notificacaoPushService.notificarEstabelecimento(agendamento.getEstabelecimentoId(),
                "Agendamento confirmado!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de confirmação: " + e.getMessage());
        }
    }
}
