package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificacaoPushService {
    private final Map<UUID, SseEmitter> clienteEmitters = new ConcurrentHashMap<>();
    private final Map<UUID, SseEmitter> profissionalEmitters = new ConcurrentHashMap<>();
    private final Map<UUID, SseEmitter> estabelecimentoEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscriberCliente(UUID clienteId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clienteEmitters.put(clienteId, emitter);

        emitter.onCompletion(() -> clienteEmitters.remove(clienteId));
        emitter.onTimeout(() -> clienteEmitters.remove(clienteId));

        return emitter;
    }

    public SseEmitter subscriberProfissional(UUID profissionalId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        profissionalEmitters.put(profissionalId, emitter);

        emitter.onCompletion(() -> profissionalEmitters.remove(profissionalId));
        emitter.onTimeout(() -> profissionalEmitters.remove(profissionalId));

        return emitter;
    }

    public SseEmitter subscriberEstabelecimento(UUID estabelecimentoId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        estabelecimentoEmitters.put(estabelecimentoId, emitter);

        emitter.onCompletion(() -> estabelecimentoEmitters.remove(estabelecimentoId));
        emitter.onTimeout(() -> estabelecimentoEmitters.remove(estabelecimentoId));

        return emitter;
    }

    public void notificarNovoAgendamento(Agendamento agendamento) {
        notificarCliente(agendamento.getClienteId(),
            "Novo agendamento criado com sucesso!");

        notificarProfissional(agendamento.getProfissionalId(),
            "Novo agendamento recebido!");

        notificarEstabelecimento(agendamento.getEstabelecimentoId(),
            "Novo agendamento registrado!");
    }

    public void notificarCancelamento(Agendamento agendamento) {
        notificarCliente(agendamento.getClienteId(),
            "Agendamento cancelado!");

        notificarProfissional(agendamento.getProfissionalId(),
            "Agendamento cancelado pelo cliente!");

        notificarEstabelecimento(agendamento.getEstabelecimentoId(),
            "Agendamento cancelado!");
    }

    public void notificarReagendamento(Agendamento agendamento) {
        notificarCliente(agendamento.getClienteId(),
            "Agendamento reagendado com sucesso!");

        notificarProfissional(agendamento.getProfissionalId(),
            "Agendamento reagendado!");

        notificarEstabelecimento(agendamento.getEstabelecimentoId(),
            "Agendamento reagendado!");
    }

    public void notificarCliente(UUID clienteId, String mensagem) {
        SseEmitter emitter = clienteEmitters.get(clienteId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("mensagem")
                    .data(mensagem));
            } catch (IOException e) {
                clienteEmitters.remove(clienteId);
            }
        }
    }

    public void notificarProfissional(UUID profissionalId, String mensagem) {
        SseEmitter emitter = profissionalEmitters.get(profissionalId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("mensagem")
                    .data(mensagem));
            } catch (IOException e) {
                profissionalEmitters.remove(profissionalId);
            }
        }
    }

    public void notificarEstabelecimento(UUID estabelecimentoId, String mensagem) {
        SseEmitter emitter = estabelecimentoEmitters.get(estabelecimentoId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("mensagem")
                    .data(mensagem));
            } catch (IOException e) {
                estabelecimentoEmitters.remove(estabelecimentoId);
            }
        }
    }
}
