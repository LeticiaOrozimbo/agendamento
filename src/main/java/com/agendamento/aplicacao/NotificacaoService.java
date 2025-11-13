package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.Agendamento;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Service
@Profile("!dev") // Ativo em todos os perfis exceto desenvolvimento
public class NotificacaoService implements INotificacaoService {
    private final JavaMailSender emailSender;
    private final TaskScheduler taskScheduler;
    private final CatalogoService catalogoService;
    private final Map<UUID, ScheduledFuture<?>> lembretes = new HashMap<>();

    public NotificacaoService(JavaMailSender emailSender,
                            TaskScheduler taskScheduler,
                            CatalogoService catalogoService) {
        this.emailSender = emailSender;
        this.taskScheduler = taskScheduler;
        this.catalogoService = catalogoService;
    }

    public void agendarLembretes(Agendamento agendamento) {
        var cliente = catalogoService.obterCliente(agendamento.getClienteId());
        var profissional = catalogoService.obterProfissional(agendamento.getProfissionalId());
        var servico = catalogoService.obterServico(agendamento.getServicoId());
        var estabelecimento = catalogoService.obterEstabelecimento(agendamento.getEstabelecimentoId());

        // Lembrete 24 horas antes
        agendarLembrete(
            agendamento.getId(),
            agendamento.getInicio().minus(24, ChronoUnit.HOURS),
            cliente.getEmail(),
            profissional.getEmail(),
            String.format("Lembrete: Agendamento amanhã - %s", servico.getNome()),
            String.format("""
                Olá! Este é um lembrete do seu agendamento:
                
                Serviço: %s
                Profissional: %s
                Local: %s
                Data/Hora: %s
                
                Em caso de necessidade de cancelamento, favor avisar com antecedência.
                """,
                servico.getNome(),
                profissional.getNome(),
                estabelecimento.getNome(),
                agendamento.getInicio()
            )
        );

        // Lembrete 1 hora antes
        agendarLembrete(
            agendamento.getId(),
            agendamento.getInicio().minus(1, ChronoUnit.HOURS),
            cliente.getEmail(),
            profissional.getEmail(),
            String.format("Lembrete: Agendamento em 1 hora - %s", servico.getNome()),
            String.format("""
                Seu agendamento é em 1 hora!
                
                Serviço: %s
                Profissional: %s
                Local: %s
                Horário: %s
                """,
                servico.getNome(),
                profissional.getNome(),
                estabelecimento.getNome(),
                agendamento.getInicio()
            )
        );
    }

    public void cancelarLembretes(UUID agendamentoId) {
        var lembrete = lembretes.remove(agendamentoId);
        if (lembrete != null) {
            lembrete.cancel(false);
        }
    }

    private void agendarLembrete(UUID agendamentoId, Instant momento,
                               String emailCliente, String emailProfissional,
                               String assunto, String mensagem) {
        ScheduledFuture<?> tarefa = taskScheduler.schedule(
            () -> enviarLembrete(emailCliente, emailProfissional, assunto, mensagem),
            momento
        );
        lembretes.put(agendamentoId, tarefa);
    }

    private void enviarLembrete(String emailCliente, String emailProfissional,
                              String assunto, String mensagem) {
        try {
            // Enviar para o cliente
            var mensagemCliente = new SimpleMailMessage();
            mensagemCliente.setTo(emailCliente);
            mensagemCliente.setSubject(assunto);
            mensagemCliente.setText(mensagem);
            emailSender.send(mensagemCliente);

            // Enviar para o profissional
            var mensagemProfissional = new SimpleMailMessage();
            mensagemProfissional.setTo(emailProfissional);
            mensagemProfissional.setSubject(assunto + " (Profissional)");
            mensagemProfissional.setText(mensagem);
            emailSender.send(mensagemProfissional);

            System.out.println("📧 Email enviado com sucesso: " + assunto);
        } catch (Exception e) {
            System.err.println("📧 Erro ao enviar email (modo desenvolvimento): " + e.getMessage());
            // Em desenvolvimento, não falha - apenas loga o erro
        }
    }
}
