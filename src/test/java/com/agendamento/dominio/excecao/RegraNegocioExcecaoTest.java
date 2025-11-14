package com.agendamento.dominio.excecao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RegraNegocioExcecao - Testes unitários")
class RegraNegocioExcecaoTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Erro de regra de negócio";

        RegraNegocioExcecao excecao = new RegraNegocioExcecao(mensagem);

        assertThat(excecao).isNotNull();
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
        assertThat(excecao).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        String mensagem = "Erro de regra de negócio";
        Throwable causa = new IllegalArgumentException("Argumento inválido");

        RegraNegocioExcecao excecao = new RegraNegocioExcecao(mensagem, causa);

        assertThat(excecao).isNotNull();
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
        assertThat(excecao.getCause()).isEqualTo(causa);
        assertThat(excecao).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve propagar exceção quando lançada")
    void devePropagaExcecaoQuandoLancada() {
        String mensagem = "Teste de propagação";

        assertThatThrownBy(() -> {
            throw new RegraNegocioExcecao(mensagem);
        })
        .isInstanceOf(RegraNegocioExcecao.class)
        .hasMessage(mensagem);
    }
}
