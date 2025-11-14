package com.agendamento.dominio.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Cliente - Testes unitários")
class ClienteTest {

    @Test
    @DisplayName("Deve criar cliente válido")
    void deveCriarClienteValido() {
        String nome = "João Silva";
        String email = "joao@email.com";
        String telefone = "+5511999999999";
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        Cliente cliente = Cliente.criar(nome, email, telefone, endereco);

        assertThat(cliente).isNotNull();
        assertThat(cliente.getId()).isNotNull();
        assertThat(cliente.getNome()).isEqualTo(nome);
        assertThat(cliente.getEmail()).isEqualTo(email);
        assertThat(cliente.getTelefone()).isEqualTo(telefone);
        assertThat(cliente.getEndereco()).isEqualTo(endereco);
        assertThat(cliente.getCalendarioIntegrado()).isFalse();
        assertThat(cliente.getEmailCalendario()).isNull();
    }

    @Test
    @DisplayName("Não deve criar cliente com nome nulo")
    void naoDeveCriarClienteComNomeNulo() {
        String nome = null;
        String email = "joao@email.com";
        String telefone = "+5511999999999";
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        assertThatThrownBy(() -> Cliente.criar(nome, email, telefone, endereco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar cliente com nome vazio")
    void naoDeveCriarClienteComNomeVazio() {
        String nome = "   ";
        String email = "joao@email.com";
        String telefone = "+5511999999999";
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        assertThatThrownBy(() -> Cliente.criar(nome, email, telefone, endereco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar cliente com email inválido")
    void naoDeveCriarClienteComEmailInvalido() {
        String nome = "João Silva";
        String email = "email-invalido";
        String telefone = "+5511999999999";
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        assertThatThrownBy(() -> Cliente.criar(nome, email, telefone, endereco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido");
    }

    @Test
    @DisplayName("Não deve criar cliente com telefone inválido")
    void naoDeveCriarClienteComTelefoneInvalido() {
        String nome = "João Silva";
        String email = "joao@email.com";
        String telefone = "123";
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        assertThatThrownBy(() -> Cliente.criar(nome, email, telefone, endereco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Telefone inválido");
    }

    @Test
    @DisplayName("Não deve criar cliente com endereço nulo")
    void naoDeveCriarClienteComEnderecoNulo() {
        String nome = "João Silva";
        String email = "joao@email.com";
        String telefone = "+5511999999999";
        Endereco endereco = null;

        assertThatThrownBy(() -> Cliente.criar(nome, email, telefone, endereco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Endereço é obrigatório");
    }

    @Test
    @DisplayName("Deve integrar calendário corretamente")
    void deveIntegrarCalendarioCorretamente() {
        Cliente cliente = Cliente.criar("João Silva", "joao@email.com", "+5511999999999",
                Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567"));
        String emailCalendario = "calendario@email.com";

        cliente.integrarCalendario(emailCalendario);

        assertThat(cliente.getCalendarioIntegrado()).isTrue();
        assertThat(cliente.getEmailCalendario()).isEqualTo(emailCalendario);
    }

    @Test
    @DisplayName("Deve remover integração de calendário")
    void deveRemoverIntegracaoCalendario() {
        Cliente cliente = Cliente.criar("João Silva", "joao@email.com", "+5511999999999",
                Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567"));
        cliente.integrarCalendario("calendario@email.com");

        cliente.removerIntegracaoCalendario();

        assertThat(cliente.getCalendarioIntegrado()).isFalse();
        assertThat(cliente.getEmailCalendario()).isNull();
    }
}
