package com.agendamento.dominio.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "servicos")
@Getter
@NoArgsConstructor
public class Servico {
    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private double preco;

    @Column(nullable = false, name = "duracao")
    private int duracaoMinutos;

    private Servico(UUID id, String nome, String descricao,
                   String categoria, double preco, int duracaoMinutos) {
        this.validarCampos(nome, categoria, preco, duracaoMinutos);

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
    }

    public static Servico criar(String nome, String descricao,
                              String categoria, double preco, int duracaoMinutos) {
        return new Servico(UUID.randomUUID(), nome, descricao, categoria, preco, duracaoMinutos);
    }

    private void validarCampos(String nome, String categoria, double preco, int duracaoMinutos) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração deve ser positiva");
        }
    }

    // Setters
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        this.categoria = categoria;
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.preco = preco;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração deve ser positiva");
        }
        this.duracaoMinutos = duracaoMinutos;
    }

    // Método de conveniência para compatibilidade
    public Duration getDuracao() {
        return Duration.ofMinutes(this.duracaoMinutos);
    }

    public void setDuracao(Duration duracao) {
        if (duracao == null || duracao.isNegative() || duracao.isZero()) {
            throw new IllegalArgumentException("Duração deve ser positiva");
        }
        this.duracaoMinutos = (int) duracao.toMinutes();
    }
}
