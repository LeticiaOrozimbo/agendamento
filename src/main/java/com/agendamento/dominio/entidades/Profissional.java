package com.agendamento.dominio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "profissionais")
public class Profissional {
    @Id
    @Column(updatable = false)
    private final UUID id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String especialidades;

    @ElementCollection
    @CollectionTable(
        name = "profissional_servicos",
        joinColumns = @JoinColumn(name = "profissional_id")
    )
    @Column(name = "servico_id")
    private Set<UUID> servicosIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "profissional_disponibilidade",
        joinColumns = @JoinColumn(name = "profissional_id")
    )
    private Set<DisponibilidadeHorario> disponibilidade = new HashSet<>();

    @Column(name = "tarifa_base", nullable = false)
    private double tarifaBase;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profissional_id", insertable = false, updatable = false)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @Column(name = "avaliacao_media", precision = 5, scale = 2)
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @Column
    private String foto;

    @Column(name = "calendario_integrado")
    private boolean calendarioIntegrado;

    @Column(name = "email_calendario")
    private String emailCalendario;

    @Column(unique = true)
    private String email;

    protected Profissional() {
        // Required by JPA
        this.id = null;
        this.servicosIds = new HashSet<>();
        this.disponibilidade = new HashSet<>();
        this.avaliacoes = new ArrayList<>();
    }

    private Profissional(UUID id, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.id = id;
        this.nome = nome;
        this.servicosIds = new HashSet<>();
        this.disponibilidade = new HashSet<>();
        this.avaliacoes = new ArrayList<>();
        this.avaliacaoMedia = BigDecimal.ZERO;
        this.calendarioIntegrado = false;
    }

    public static Profissional criar(String nome) {
        return new Profissional(UUID.randomUUID(), nome);
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação é obrigatória");
        }
        this.avaliacoes.add(avaliacao);
        this.atualizarMediaAvaliacoes();
    }

    private void atualizarMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) {
            this.avaliacaoMedia = BigDecimal.ZERO;
        } else {
            this.avaliacaoMedia = this.avaliacoes.stream()
                .map(a -> BigDecimal.valueOf(a.getEstrelas()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(avaliacoes.size()), 2, RoundingMode.HALF_UP);
        }
    }

    public void adicionarServico(UUID servicoId) {
        if (servicoId == null) {
            throw new IllegalArgumentException("ID do serviço é obrigatório");
        }
        this.servicosIds.add(servicoId);
    }

    public void removerServico(UUID servicoId) {
        this.servicosIds.remove(servicoId);
    }

    public void adicionarDisponibilidade(DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        this.disponibilidade.add(new DisponibilidadeHorario(dia, inicio, fim));
    }

    public void removerDisponibilidade(DayOfWeek dia) {
        this.disponibilidade.removeIf(d -> d.getDiaSemana() == dia);
    }

    public void integrarCalendario(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido para integração com calendário");
        }
        this.calendarioIntegrado = true;
        this.emailCalendario = email;
    }

    public void removerIntegracaoCalendario() {
        this.calendarioIntegrado = false;
        this.emailCalendario = null;
    }

    // Getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidades() { return especialidades; }
    public String getFoto() {
        return foto;
    }

    public double getTarifaBase() { return tarifaBase; }
    public Set<UUID> getServicosIds() { return Collections.unmodifiableSet(servicosIds); }
    public Set<DisponibilidadeHorario> getDisponibilidade() {
        return Collections.unmodifiableSet(disponibilidade);
    }
    public List<Avaliacao> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }
    public BigDecimal getAvaliacaoMedia() { return avaliacaoMedia; }
    public String getEmail() { return emailCalendario != null ? emailCalendario : email; }
    public boolean getCalendarioIntegrado() { return calendarioIntegrado; }

    // Setters
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.nome = nome;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public void setTarifaBase(double tarifa) {
        if (tarifa < 0) {
            throw new IllegalArgumentException("Tarifa não pode ser negativa");
        }
        this.tarifaBase = tarifa;
    }

    public void definirFoto(String foto) {
        this.foto = foto;
    }

    @Embeddable
    public static class DisponibilidadeHorario {
        @Column(name = "dia_semana", nullable = false)
        @Enumerated(EnumType.ORDINAL)
        private DayOfWeek diaSemana;

        @Column(name = "horario_inicio", nullable = false)
        private LocalTime inicio;

        @Column(name = "horario_fim", nullable = false)
        private LocalTime fim;

        protected DisponibilidadeHorario() {
            // Required by JPA
        }

        public DisponibilidadeHorario(DayOfWeek diaSemana, LocalTime inicio, LocalTime fim) {
            if (diaSemana == null || inicio == null || fim == null) {
                throw new IllegalArgumentException("Dia da semana e horários são obrigatórios");
            }
            if (inicio.isAfter(fim)) {
                throw new IllegalArgumentException("Horário inicial deve ser anterior ao final");
            }
            this.diaSemana = diaSemana;
            this.inicio = inicio;
            this.fim = fim;
        }

        public DayOfWeek getDiaSemana() { return diaSemana; }
        public LocalTime getInicio() { return inicio; }
        public LocalTime getFim() { return fim; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DisponibilidadeHorario that = (DisponibilidadeHorario) o;
            return diaSemana == that.diaSemana &&
                   Objects.equals(inicio, that.inicio) &&
                   Objects.equals(fim, that.fim);
        }

        @Override
        public int hashCode() {
            return Objects.hash(diaSemana, inicio, fim);
        }
    }
}
