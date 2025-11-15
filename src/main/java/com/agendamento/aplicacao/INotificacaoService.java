package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import java.util.UUID;

public interface INotificacaoService {
    void agendarLembretes(Agendamento agendamento);
    void cancelarLembretes(UUID agendamentoId);
}
