package com.agendamento.infraestrutura.dto.response;

import com.agendamento.dominio.entidades.Avaliacao;
import com.agendamento.dominio.entidades.Profissional;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ProfissionalResponseDTO {
    private final UUID id;
    private final String nome;
    private final String especialidades;
    private final String foto;
    private final double tarifaBase;
    private final List<UUID> servicosIds;
    private final List<DisponibilidadeHorarioDTO> disponibilidade;
    private final List<AvaliacaoDTO> avaliacoes;
    private final double avaliacaoMedia;
    private final boolean calendarioIntegrado;

    public ProfissionalResponseDTO(Profissional profissional) {
        this.id = profissional.getId();
        this.nome = profissional.getNome();
        this.especialidades = profissional.getEspecialidades();
        this.foto = profissional.getFoto();
        this.tarifaBase = profissional.getTarifaBase();
        this.servicosIds = profissional.getServicosIds().stream().toList();
        this.disponibilidade = profissional.getDisponibilidade().stream()
            .map(dh -> new DisponibilidadeHorarioDTO(dh.getDiaSemana(), dh.getInicio(), dh.getFim()))
            .toList();
        this.avaliacoes = profissional.getAvaliacoes().stream()
            .map(AvaliacaoDTO::new)
            .toList();
        this.avaliacaoMedia = profissional.getAvaliacaoMedia() != null ? profissional.getAvaliacaoMedia().doubleValue() : 0.0;
        this.calendarioIntegrado = profissional.getCalendarioIntegrado();
    }

    @Getter
    public static class DisponibilidadeHorarioDTO {
        private final DayOfWeek diaSemana;
        private final LocalTime inicio;
        private final LocalTime fim;

        public DisponibilidadeHorarioDTO(DayOfWeek diaSemana, LocalTime inicio, LocalTime fim) {
            this.diaSemana = diaSemana;
            this.inicio = inicio;
            this.fim = fim;
        }
    }

    @Getter
    public static class AvaliacaoDTO {
        private final UUID clienteId;
        private final int estrelas;
        private final String comentario;

        public AvaliacaoDTO(Avaliacao avaliacao) {
            this.clienteId = avaliacao.getClienteId();
            this.estrelas = avaliacao.getEstrelas();
            this.comentario = avaliacao.getComentario();
        }
    }
}
