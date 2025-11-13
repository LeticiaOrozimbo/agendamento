package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import java.util.UUID;

/**
 * Interface comum para serviços de notificação
 * Permite usar diferentes implementações (real ou mock) dependendo do ambiente
 */
public interface INotificacaoService {

    /**
     * Agenda lembretes automáticos para um agendamento
     */
    void agendarLembretes(Agendamento agendamento);

    /**
     * Cancela lembretes de um agendamento
     */
    void cancelarLembretes(UUID agendamentoId);
}
