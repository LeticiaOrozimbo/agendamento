package com.agendamento.dominio.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Servico - Testes unitários")
class ServicoTest {

    @Test
    @DisplayName("Deve criar serviço válido")
    void deveCriarServicoValido() {
        String nome = "Corte de Cabelo";
        String descricao = "Corte masculino moderno";
        String categoria = "Cabelo";
        double preco = 50.0;
        int duracaoMinutos = 60;

        Servico servico = Servico.criar(nome, descricao, categoria, preco, duracaoMinutos);

        assertThat(servico).isNotNull();
        assertThat(servico.getId()).isNotNull();
        assertThat(servico.getNome()).isEqualTo(nome);
        assertThat(servico.getDescricao()).isEqualTo(descricao);
        assertThat(servico.getCategoria()).isEqualTo(categoria);
        assertThat(servico.getPreco()).isEqualTo(preco);
        assertThat(servico.getDuracaoMinutos()).isEqualTo(duracaoMinutos);
    }

    @Test
    @DisplayName("Não deve criar serviço com nome nulo")
    void naoDeveCriarServicoComNomeNulo() {
        assertThatThrownBy(() -> Servico.criar(null, "Descrição", "Categoria", 50.0, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar serviço com nome vazio")
    void naoDeveCriarServicoComNomeVazio() {
        assertThatThrownBy(() -> Servico.criar("   ", "Descrição", "Categoria", 50.0, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar serviço com categoria nula")
    void naoDeveCriarServicoComCategoriaNula() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Descrição", null, 50.0, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Categoria é obrigatória");
    }

    @Test
    @DisplayName("Não deve criar serviço com categoria vazia")
    void naoDeveCriarServicoComCategoriaVazia() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Descrição", "   ", 50.0, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Categoria é obrigatória");
    }

    @Test
    @DisplayName("Não deve criar serviço com preço negativo")
    void naoDeveCriarServicoComPrecoNegativo() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Descrição", "Categoria", -10.0, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço não pode ser negativo");
    }

    @Test
    @DisplayName("Não deve criar serviço com duração zero")
    void naoDeveCriarServicoComDuracaoZero() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Descrição", "Categoria", 50.0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duração deve ser positiva");
    }

    @Test
    @DisplayName("Não deve criar serviço com duração negativa")
    void naoDeveCriarServicoComDuracaoNegativa() {
        assertThatThrownBy(() -> Servico.criar("Nome", "Descrição", "Categoria", 50.0, -30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duração deve ser positiva");
    }

    @Test
    @DisplayName("Deve aceitar preço zero")
    void deveAceitarPrecoZero() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 0.0, 60);

        assertThat(servico.getPreco()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve criar serviço sem descrição")
    void deveCriarServicoSemDescricao() {
        Servico servico = Servico.criar("Nome", null, "Categoria", 50.0, 60);

        assertThat(servico.getDescricao()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar nome válido")
    void deveAtualizarNomeValido() {
        Servico servico = Servico.criar("Nome Original", "Descrição", "Categoria", 50.0, 60);
        String novoNome = "Novo Nome";

        servico.setNome(novoNome);

        assertThat(servico.getNome()).isEqualTo(novoNome);
    }

    @Test
    @DisplayName("Não deve atualizar nome para nulo")
    void naoDeveAtualizarNomeParaNulo() {
        Servico servico = Servico.criar("Nome Original", "Descrição", "Categoria", 50.0, 60);

        assertThatThrownBy(() -> servico.setNome(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Deve atualizar preço válido")
    void deveAtualizarPrecoValido() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 50.0, 60);
        double novoPreco = 75.0;

        servico.setPreco(novoPreco);

        assertThat(servico.getPreco()).isEqualTo(novoPreco);
    }

    @Test
    @DisplayName("Não deve atualizar preço para valor negativo")
    void naoDeveAtualizarPrecoParaValorNegativo() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 50.0, 60);

        assertThatThrownBy(() -> servico.setPreco(-10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preço não pode ser negativo");
    }

    @Test
    @DisplayName("Deve atualizar duração válida")
    void deveAtualizarDuracaoValida() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 50.0, 60);
        int novaDuracao = 90;

        servico.setDuracaoMinutos(novaDuracao);

        assertThat(servico.getDuracaoMinutos()).isEqualTo(novaDuracao);
    }

    @Test
    @DisplayName("Não deve atualizar duração para valor não positivo")
    void naoDeveAtualizarDuracaoParaValorNaoPositivo() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 50.0, 60);

        assertThatThrownBy(() -> servico.setDuracaoMinutos(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duração deve ser positiva");
    }

    @Test
    @DisplayName("Deve retornar duração como Duration")
    void deveRetornarDuracaoComoDuration() {
        Servico servico = Servico.criar("Nome", "Descrição", "Categoria", 50.0, 90);
        Duration duracao = servico.getDuracao();
        assertThat(duracao).isEqualTo(Duration.ofMinutes(90));
    }
}
