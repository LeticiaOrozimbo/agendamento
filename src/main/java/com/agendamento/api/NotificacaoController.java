package com.agendamento.api;

import com.agendamento.aplicacao.NotificacaoPushService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {
    private final NotificacaoPushService notificacaoPushService;

    public NotificacaoController(NotificacaoPushService notificacaoPushService) {
        this.notificacaoPushService = notificacaoPushService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "Notificação Service");
        status.put("status", "Ativo");
        status.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping(value = "/cliente/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeCliente(@PathVariable UUID id) {
        return notificacaoPushService.subscriberCliente(id);
    }



    @PostMapping("/teste/{clienteId}")
    public ResponseEntity<Map<String, String>> testeNotificacao(@PathVariable UUID clienteId) {
        Map<String, String> response = new HashMap<>();
        try {
            notificacaoPushService.notificarCliente(clienteId, "Teste de notificação");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

