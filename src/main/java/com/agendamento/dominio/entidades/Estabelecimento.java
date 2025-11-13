package com.agendamento.dominio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "estabelecimentos")
public class Estabelecimento {
    @Id
    @Column(updatable = false)
    private final UUID id;

    @Column(nullable = false)
    private String nome;

    @Embedded
    private Endereco endereco;

    @Column
    private String descricao;

    @Column(name = "avaliacao_media", precision = 5, scale = 2)
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(
        name = "estabelecimento_fotos",
        joinColumns = @JoinColumn(name = "estabelecimento_id")
    )
    @Column(name = "url")
    private List<String> fotos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "estabelecimento_horarios",
        joinColumns = @JoinColumn(name = "estabelecimento_id")
    )
    @MapKeyColumn(name = "dia_semana")
    @MapKeyEnumerated(EnumType.ORDINAL)
    private Map<DayOfWeek, HorarioFuncionamento> horariosFuncionamento = new HashMap<>();

    @ElementCollection
    @CollectionTable(
        name = "estabelecimento_servicos",
        joinColumns = @JoinColumn(name = "estabelecimento_id")
    )
    @Column(name = "servico_id")
    private Set<UUID> servicosIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
        name = "estabelecimento_profissionais",
        joinColumns = @JoinColumn(name = "estabelecimento_id")
    )
    @Column(name = "profissional_id")
    private Set<UUID> profissionaisIds = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "estabelecimento_id", insertable = false, updatable = false)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    protected Estabelecimento() {
        // Required by JPA
        this.id = null;
        this.fotos = new ArrayList<>();
        this.horariosFuncionamento = new HashMap<>();
        this.servicosIds = new HashSet<>();
        this.profissionaisIds = new HashSet<>();
        this.avaliacoes = new ArrayList<>();
    }

    private Estabelecimento(UUID id, String nome, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.fotos = new ArrayList<>();
        this.horariosFuncionamento = new HashMap<>();
        this.servicosIds = new HashSet<>();
        this.profissionaisIds = new HashSet<>();
        this.avaliacaoMedia = BigDecimal.ZERO;
        this.avaliacoes = new ArrayList<>();
    }

    public static Estabelecimento criar(String nome, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        return new Estabelecimento(UUID.randomUUID(), nome, endereco);
    }

    public void adicionarFoto(String urlFoto) {
        if (urlFoto == null || urlFoto.trim().isEmpty()) {
            throw new IllegalArgumentException("URL da foto é obrigatória");
        }
        this.fotos.add(urlFoto);
    }

    public void removerFoto(String urlFoto) {
        this.fotos.remove(urlFoto);
    }

    public void definirHorario(DayOfWeek dia, LocalTime abertura, LocalTime fechamento) {
        if (abertura.isAfter(fechamento)) {
            throw new IllegalArgumentException("Horário de abertura deve ser anterior ao de fechamento");
        }
        this.horariosFuncionamento.put(dia, new HorarioFuncionamento(abertura, fechamento));
    }

    public void removerHorario(DayOfWeek dia) {
        this.horariosFuncionamento.remove(dia);
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

    public void adicionarProfissional(UUID profissionalId) {
        if (profissionalId == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }
        this.profissionaisIds.add(profissionalId);
    }

    public void removerProfissional(UUID profissionalId) {
        this.profissionaisIds.remove(profissionalId);
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
            this.avaliacaoMedia = avaliacoes.stream()
                .map(a -> BigDecimal.valueOf(a.getEstrelas()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(avaliacoes.size()), 2, RoundingMode.HALF_UP);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public Endereco getEndereco() { return endereco; }
    public String getDescricao() { return descricao; }
    public List<String> getFotos() { return Collections.unmodifiableList(fotos); }
    public Map<DayOfWeek, HorarioFuncionamento> getHorariosFuncionamento() {
        return Collections.unmodifiableMap(horariosFuncionamento);
    }
    public Set<UUID> getServicosIds() { return Collections.unmodifiableSet(servicosIds); }
    public Set<UUID> getProfissionaisIds() { return Collections.unmodifiableSet(profissionaisIds); }
    public BigDecimal getAvaliacaoMedia() { return avaliacaoMedia; }
    public List<Avaliacao> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }

    // Setters
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.nome = nome;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        this.endereco = endereco;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Embeddable
    public static class HorarioFuncionamento {
        @Column(name = "horario_abertura", nullable = false)
        private LocalTime abertura;

        @Column(name = "horario_fechamento", nullable = false)
        private LocalTime fechamento;

        protected HorarioFuncionamento() {
            // Required by JPA
        }

        public HorarioFuncionamento(LocalTime abertura, LocalTime fechamento) {
            if (abertura == null || fechamento == null) {
                throw new IllegalArgumentException("Horários são obrigatórios");
            }
            if (abertura.isAfter(fechamento)) {
                throw new IllegalArgumentException("Horário de abertura deve ser anterior ao de fechamento");
            }
            this.abertura = abertura;
            this.fechamento = fechamento;
        }

        public LocalTime getAbertura() { return abertura; }
        public LocalTime getFechamento() { return fechamento; }
    }
}
