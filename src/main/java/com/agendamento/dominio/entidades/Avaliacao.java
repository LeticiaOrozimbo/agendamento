package com.agendamento.dominio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
@Getter
@NoArgsConstructor
public class Avaliacao {
    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "estabelecimento_id")
    private UUID estabelecimentoId;

    @Column(name = "profissional_id")
    private UUID profissionalId;

    @Column(nullable = false)
    private int estrelas;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "data_avaliacao", nullable = false)
    private Instant dataAvaliacao;

    private Avaliacao(UUID id, UUID clienteId, UUID estabelecimentoId, UUID profissionalId, int estrelas, String comentario) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        if (estabelecimentoId == null && profissionalId == null) {
            throw new IllegalArgumentException("Deve avaliar estabelecimento ou profissional");
        }
        if (estrelas < 1 || estrelas > 5) {
            throw new IllegalArgumentException("Avaliação deve ser entre 1 e 5 estrelas");
        }

        this.id = id;
        this.clienteId = clienteId;
        this.estabelecimentoId = estabelecimentoId;
        this.profissionalId = profissionalId;
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.dataAvaliacao = Instant.now();
    }

    public static Avaliacao criarParaEstabelecimento(UUID clienteId, UUID estabelecimentoId, int estrelas, String comentario) {
        return new Avaliacao(UUID.randomUUID(), clienteId, estabelecimentoId, null, estrelas, comentario);
    }

    public static Avaliacao criarParaProfissional(UUID clienteId, UUID profissionalId, int estrelas, String comentario) {
        return new Avaliacao(UUID.randomUUID(), clienteId, null, profissionalId, estrelas, comentario);
    }

    public static Avaliacao criar(UUID clienteId, int estrelas, String comentario) {
        throw new UnsupportedOperationException("Use criarParaEstabelecimento ou criarParaProfissional");
    }

    public Instant getData() {
        return this.dataAvaliacao;
    }
}
