package com.agendamento.dominio.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Avaliacao - Testes unitários")
class AvaliacaoTest {

    @Test
    @DisplayName("Deve criar avaliação para estabelecimento")
    void deveCriarAvaliacaoParaEstabelecimento() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        int estrelas = 5;
        String comentario = "Excelente atendimento!";

        Avaliacao avaliacao = Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, estrelas, comentario);

        assertThat(avaliacao).isNotNull();
        assertThat(avaliacao.getId()).isNotNull();
        assertThat(avaliacao.getClienteId()).isEqualTo(clienteId);
        assertThat(avaliacao.getEstabelecimentoId()).isEqualTo(estabelecimentoId);
        assertThat(avaliacao.getProfissionalId()).isNull();
        assertThat(avaliacao.getEstrelas()).isEqualTo(estrelas);
        assertThat(avaliacao.getComentario()).isEqualTo(comentario);
        assertThat(avaliacao.getDataAvaliacao()).isNotNull();
        assertThat(avaliacao.getDataAvaliacao()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Deve criar avaliação para profissional")
    void deveCriarAvaliacaoParaProfissional() {
        UUID clienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        int estrelas = 4;
        String comentario = "Muito bom profissional!";

        Avaliacao avaliacao = Avaliacao.criarParaProfissional(clienteId, profissionalId, estrelas, comentario);

        assertThat(avaliacao).isNotNull();
        assertThat(avaliacao.getId()).isNotNull();
        assertThat(avaliacao.getClienteId()).isEqualTo(clienteId);
        assertThat(avaliacao.getEstabelecimentoId()).isNull();
        assertThat(avaliacao.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(avaliacao.getEstrelas()).isEqualTo(estrelas);
        assertThat(avaliacao.getComentario()).isEqualTo(comentario);
        assertThat(avaliacao.getDataAvaliacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar avaliação sem comentário")
    void deveCriarAvaliacaoSemComentario() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        int estrelas = 3;

        Avaliacao avaliacao = Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, estrelas, null);

        assertThat(avaliacao.getComentario()).isNull();
        assertThat(avaliacao.getEstrelas()).isEqualTo(estrelas);
    }

    @Test
    @DisplayName("Não deve criar avaliação com cliente nulo")
    void naoDeveCriarAvaliacaoComClienteNulo() {
        UUID estabelecimentoId = UUID.randomUUID();

        assertThatThrownBy(() -> Avaliacao.criarParaEstabelecimento(null, estabelecimentoId, 5, "Comentário"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID do cliente é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar avaliação com estrelas menor que 1")
    void naoDeveCriarAvaliacaoComEstrelasMenorQue1() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();

        assertThatThrownBy(() -> Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, 0, "Comentário"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Avaliação deve ser entre 1 e 5 estrelas");
    }

    @Test
    @DisplayName("Não deve criar avaliação com estrelas maior que 5")
    void naoDeveCriarAvaliacaoComEstrelasMaiorQue5() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();

        assertThatThrownBy(() -> Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, 6, "Comentário"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Avaliação deve ser entre 1 e 5 estrelas");
    }

    @Test
    @DisplayName("Deve aceitar todas as estrelas válidas")
    void deveAceitarTodasAsEstrelasValidas() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();

        for (int estrelas = 1; estrelas <= 5; estrelas++) {
            Avaliacao avaliacao = Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, estrelas, "Comentário");
            assertThat(avaliacao.getEstrelas()).isEqualTo(estrelas);
        }
    }

    @Test
    @DisplayName("Método getData deve retornar dataAvaliacao")
    void metodoGetDataDeveRetornarDataAvaliacao() {
        UUID clienteId = UUID.randomUUID();
        UUID estabelecimentoId = UUID.randomUUID();
        Avaliacao avaliacao = Avaliacao.criarParaEstabelecimento(clienteId, estabelecimentoId, 5, "Comentário");

        Instant data = avaliacao.getData();

        assertThat(data).isEqualTo(avaliacao.getDataAvaliacao());
    }

    @Test
    @DisplayName("Método criar antigo deve lançar exceção")
    void metodoCriarAntigoDeveLancarExcecao() {
        UUID clienteId = UUID.randomUUID();

        assertThatThrownBy(() -> Avaliacao.criar(clienteId, 5, "Comentário"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Use criarParaEstabelecimento ou criarParaProfissional");
    }
}
