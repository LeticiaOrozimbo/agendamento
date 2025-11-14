package com.agendamento.dominio.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Endereco - Testes unitários")
class EnderecoTest {

    @Test
    @DisplayName("Deve criar endereço válido")
    void deveCriarEnderecoValido() {
        String logradouro = "Rua das Flores";
        String numero = "123";
        String complemento = "Apto 1";
        String bairro = "Centro";
        String cidade = "São Paulo";
        String estado = "SP";
        String cep = "01234567";

        Endereco endereco = Endereco.criar(logradouro, numero, complemento, bairro, cidade, estado, cep);

        assertThat(endereco).isNotNull();
        assertThat(endereco.getLogradouro()).isEqualTo(logradouro);
        assertThat(endereco.getNumero()).isEqualTo(numero);
        assertThat(endereco.getComplemento()).isEqualTo(complemento);
        assertThat(endereco.getBairro()).isEqualTo(bairro);
        assertThat(endereco.getCidade()).isEqualTo(cidade);
        assertThat(endereco.getEstado()).isEqualTo(estado);
        assertThat(endereco.getCep()).isEqualTo(cep);
    }

    @Test
    @DisplayName("Não deve criar endereço com logradouro nulo")
    void naoDeveCriarEnderecoComLogradouroNulo() {
        assertThatThrownBy(() -> Endereco.criar(null, "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Logradouro é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar endereço com logradouro vazio")
    void naoDeveCriarEnderecoComLogradouroVazio() {
        assertThatThrownBy(() -> Endereco.criar("   ", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Logradouro é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar endereço com número nulo")
    void naoDeveCriarEnderecoComNumeroNulo() {
        assertThatThrownBy(() -> Endereco.criar("Rua A", null, "Apto 1", "Centro", "São Paulo", "SP", "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Número é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar endereço com bairro nulo")
    void naoDeveCriarEnderecoComBairroNulo() {
        assertThatThrownBy(() -> Endereco.criar("Rua A", "123", "Apto 1", null, "São Paulo", "SP", "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bairro é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar endereço com cidade nula")
    void naoDeveCriarEnderecoComCidadeNula() {
        assertThatThrownBy(() -> Endereco.criar("Rua A", "123", "Apto 1", "Centro", null, "SP", "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cidade é obrigatória");
    }

    @Test
    @DisplayName("Não deve criar endereço com estado nulo")
    void naoDeveCriarEnderecoComEstadoNulo() {
        assertThatThrownBy(() -> Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", null, "01234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estado é obrigatório");
    }

    @Test
    @DisplayName("Não deve criar endereço com CEP inválido")
    void naoDeveCriarEnderecoComCepInvalido() {
        assertThatThrownBy(() -> Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CEP deve estar no formato 00000-000 ou 00000000");
    }

    @Test
    @DisplayName("Deve criar endereço sem complemento")
    void deveCriarEnderecoSemComplemento() {
        String logradouro = "Rua das Flores";
        String numero = "123";
        String complemento = null;
        String bairro = "Centro";
        String cidade = "São Paulo";
        String estado = "SP";
        String cep = "01234567";

        Endereco endereco = Endereco.criar(logradouro, numero, complemento, bairro, cidade, estado, cep);

        assertThat(endereco).isNotNull();
        assertThat(endereco.getComplemento()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar CEP com formatação")
    void deveAceitarCepComFormatacao() {
        Endereco endereco = Endereco.criar("Rua A", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234-567");

        assertThat(endereco).isNotNull();
        assertThat(endereco.getCep()).isEqualTo("01234-567");
    }

    @Test
    @DisplayName("Deve ter todos os campos preenchidos corretamente")
    void deveTerTodosCamposPreenchidosCorretamente() {
        Endereco endereco = Endereco.criar("Rua das Flores", "123", "Apto 1", "Centro", "São Paulo", "SP", "01234567");

        assertThat(endereco.getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(endereco.getNumero()).isEqualTo("123");
        assertThat(endereco.getComplemento()).isEqualTo("Apto 1");
        assertThat(endereco.getBairro()).isEqualTo("Centro");
        assertThat(endereco.getCidade()).isEqualTo("São Paulo");
        assertThat(endereco.getEstado()).isEqualTo("SP");
        assertThat(endereco.getCep()).isEqualTo("01234567");
    }

    @Test
    @DisplayName("Deve funcionar sem complemento")
    void deveFuncionarSemComplemento() {
        Endereco endereco = Endereco.criar("Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01234567");

        assertThat(endereco.getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(endereco.getNumero()).isEqualTo("123");
        assertThat(endereco.getComplemento()).isNull();
        assertThat(endereco.getBairro()).isEqualTo("Centro");
        assertThat(endereco.getCidade()).isEqualTo("São Paulo");
        assertThat(endereco.getEstado()).isEqualTo("SP");
        assertThat(endereco.getCep()).isEqualTo("01234567");
    }
}
