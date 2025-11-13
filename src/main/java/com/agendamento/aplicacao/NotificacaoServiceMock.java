package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Profile("dev")
public class NotificacaoServiceMock implements INotificacaoService {
    private final Map<UUID, String> lembretesMock = new HashMap<>();

    public NotificacaoServiceMock() {
    }

    @Override
    public void agendarLembretes(Agendamento agendamento) {
        lembretesMock.put(agendamento.getId(), "Lembretes agendados");
    }

    @Override
    public void cancelarLembretes(UUID agendamentoId) {
        lembretesMock.remove(agendamentoId);
    }



}
