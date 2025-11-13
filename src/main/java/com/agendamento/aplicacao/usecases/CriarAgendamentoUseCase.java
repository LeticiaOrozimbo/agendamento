package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.aplicacao.INotificacaoService;
import com.agendamento.aplicacao.NotificacaoPushService;
import com.agendamento.aplicacao.CalendarioService;
import com.agendamento.infraestrutura.dto.AgendamentoDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
public class CriarAgendamentoUseCase {
    private final AgendamentoRepositorio agendamentoRepositorio;
    private final CatalogoService catalogoService;
    private final INotificacaoService notificacaoService;
    private final NotificacaoPushService notificacaoPushService;
    private final CalendarioService calendarioService;

    public CriarAgendamentoUseCase(AgendamentoRepositorio agendamentoRepositorio,
                                  CatalogoService catalogoService,
                                  INotificacaoService notificacaoService,
                                  NotificacaoPushService notificacaoPushService,
                                  CalendarioService calendarioService) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.catalogoService = catalogoService;
        this.notificacaoService = notificacaoService;
        this.notificacaoPushService = notificacaoPushService;
        this.calendarioService = calendarioService;
    }

    @Transactional
    public Agendamento executar(AgendamentoDTO dto) {
        // 1. Validar existências
        validarEntidades(dto);

        // 2. Validar regras de negócio
        validarRegrasNegocio(dto);

        // 3. Criar agendamento
        var servico = catalogoService.obterServico(dto.servicoId());
        Instant inicio = dto.inicio();
        Instant fim = inicio.plus(Duration.ofMinutes(servico.getDuracaoMinutos()));

        var agendamento = Agendamento.criar(
            dto.clienteId(),
            dto.estabelecimentoId(),
            dto.profissionalId(),
            dto.servicoId(),
            inicio,
            fim
        );

        var agendamentoSalvo = agendamentoRepositorio.salvar(agendamento);

        // Integração com calendário
        try {
            calendarioService.adicionarEvento(agendamentoSalvo);
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar com calendário: " + e.getMessage());
        }

        // Agendar lembretes por email
        notificacaoService.agendarLembretes(agendamentoSalvo);

        // Enviar notificação push
        notificacaoPushService.notificarNovoAgendamento(agendamentoSalvo);

        return agendamentoSalvo;
    }

    private void validarEntidades(AgendamentoDTO dto) {
        if (catalogoService.obterCliente(dto.clienteId()) == null) {
            throw new RegraNegocioExcecao("Cliente não encontrado");
        }

        if (catalogoService.obterEstabelecimento(dto.estabelecimentoId()) == null) {
            throw new RegraNegocioExcecao("Estabelecimento não encontrado");
        }

        if (catalogoService.obterProfissional(dto.profissionalId()) == null) {
            throw new RegraNegocioExcecao("Profissional não encontrado");
        }

        if (catalogoService.obterServico(dto.servicoId()) == null) {
            throw new RegraNegocioExcecao("Serviço não encontrado");
        }
    }

    private void validarRegrasNegocio(AgendamentoDTO dto) {
        // Validar horário de funcionamento
        var estabelecimento = catalogoService.obterEstabelecimento(dto.estabelecimentoId());
        if (!estabelecimentoEstaAberto(estabelecimento, dto.inicio())) {
            throw new RegraNegocioExcecao("Estabelecimento fechado neste horário");
        }

        // Validar disponibilidade do profissional
        var profissional = catalogoService.obterProfissional(dto.profissionalId());
        if (!profissionalEstaDisponivel(profissional, dto.inicio())) {
            throw new RegraNegocioExcecao("Profissional não disponível neste horário");
        }

        // Validar conflitos
        var servico = catalogoService.obterServico(dto.servicoId());
        Instant fim = dto.inicio().plus(Duration.ofMinutes(servico.getDuracaoMinutos()));

        var conflitos = agendamentoRepositorio.buscarConflitosParaProfissional(
            dto.profissionalId(), dto.inicio(), fim
        );

        if (!conflitos.isEmpty()) {
            throw new RegraNegocioExcecao("Conflito de agendamento para o profissional");
        }
    }

    private boolean estabelecimentoEstaAberto(com.agendamento.dominio.entidades.Estabelecimento estabelecimento, Instant momento) {
        // Implementação da validação de horário
        return true; // Simplificado para o exemplo
    }

    private boolean profissionalEstaDisponivel(com.agendamento.dominio.entidades.Profissional profissional, Instant momento) {
        // Implementação da validação de disponibilidade
        return true; // Simplificado para o exemplo
    }
}
