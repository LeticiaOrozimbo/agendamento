package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class CalendarioService {
    private static final String APPLICATION_NAME = "Sistema de Agendamentos";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final Map<String, Calendar> clientesCalendario = new HashMap<>();
    private final CatalogoService catalogoService;

    public void adicionarEvento(Agendamento agendamento) {
        try {
            var cliente = catalogoService.obterCliente(agendamento.getClienteId());
            var profissional = catalogoService.obterProfissional(agendamento.getProfissionalId());
            var servico = catalogoService.obterServico(agendamento.getServicoId());
            var estabelecimento = catalogoService.obterEstabelecimento(agendamento.getEstabelecimentoId());

            String titulo = String.format("Agendamento: %s com %s", servico.getNome(), profissional.getNome());
            String descricao = String.format("Local: %s\nProfissional: %s", estabelecimento.getNome(), profissional.getNome());

            // Criar evento para o cliente
            if (cliente.getCalendarioIntegrado()) {
                criarEventoNoCalendario(cliente.getEmail(), titulo, descricao,
                    Date.from(agendamento.getInicio()),
                    Date.from(agendamento.getFim()));
            }

            // Criar evento para o profissional
            if (profissional.getCalendarioIntegrado()) {
                String tituloProfissional = String.format("Cliente: %s - %s", cliente.getNome(), servico.getNome());
                String descricaoProfissional = String.format("Serviço: %s\nCliente: %s", servico.getNome(), cliente.getNome());

                criarEventoNoCalendario(profissional.getEmail(), tituloProfissional, descricaoProfissional,
                    Date.from(agendamento.getInicio()),
                    Date.from(agendamento.getFim()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao adicionar evento no calendário", e);
        }
    }

    public void removerEvento(Agendamento agendamento) {
        // TODO: Implementar remoção do evento do calendário quando necessário
    }

    public void atualizarEvento(Agendamento agendamento) {
        removerEvento(agendamento);
        adicionarEvento(agendamento);
    }

    private void criarEventoNoCalendario(String email, String titulo, String descricao,
                                       Date inicio, Date fim) {
        try {
            Calendar service = obterServicoCalendario(email);
            Event event = new Event()
                .setSummary(titulo)
                .setDescription(descricao);

            EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(inicio))
                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()).getID());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(fim))
                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()).getID());
            event.setEnd(end);

            String calendarId = "primary";
            service.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar evento no calendário", e);
        }
    }

    private Calendar obterServicoCalendario(String email) {
        return clientesCalendario.computeIfAbsent(email, this::criarServicoCalendario);
    }

    private Calendar criarServicoCalendario(String email) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = obterCredencial(email);

            return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao criar serviço do calendário", e);
        }
    }

    private Credential obterCredencial(String email) {
        // TODO: Implementar autenticação OAuth2 com o Google
        // Por enquanto, retornamos null para desenvolvimento
        return null;
    }
}
