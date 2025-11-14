package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Endereco;
import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarEstabelecimentosComFiltrosUseCase - Testes unitários")
class BuscarEstabelecimentosComFiltrosUseCaseTest {

    @Mock
    private EstabelecimentoRepositorio estabelecimentoRepositorio;

    private BuscarEstabelecimentosComFiltrosUseCase buscarEstabelecimentosUseCase;

    private List<Estabelecimento> estabelecimentosMock;

    @BeforeEach
    void setUp() {
        buscarEstabelecimentosUseCase = new BuscarEstabelecimentosComFiltrosUseCase(estabelecimentoRepositorio);

        estabelecimentosMock = Arrays.asList(
                criarEstabelecimento("Salão Beleza", "São Paulo", "Centro", new BigDecimal("4.5")),
                criarEstabelecimento("Studio Hair", "São Paulo", "Jardins", new BigDecimal("4.8")),
                criarEstabelecimento("Spa Relaxar", "Rio de Janeiro", "Copacabana", new BigDecimal("4.2")),
                criarEstabelecimento("Clínica Estética", "São Paulo", "Vila Madalena", new BigDecimal("4.7"))
        );
    }

    @Test
    @DisplayName("Deve buscar todos os estabelecimentos quando não há filtros")
    void deveBuscarTodosEstabelecimentosQuandoNaoHaFiltros() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, null, null, null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(4);
        assertThat(resultado).containsAll(estabelecimentosMock);
    }

    @Test
    @DisplayName("Deve filtrar por nome")
    void deveFiltrarPorNome() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                "Salão", null, null, null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).contains("Salão");
    }

    @Test
    @DisplayName("Deve filtrar por cidade")
    void deveFiltrarPorCidade() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, "São Paulo", null, null, null, null
        );

        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).allMatch(e -> e.getEndereco().getCidade().equals("São Paulo"));
    }

    @Test
    @DisplayName("Deve filtrar por bairro")
    void deveFiltrarPorBairro() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, null, "Centro", null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEndereco().getBairro()).isEqualTo("Centro");
    }

    @Test
    @DisplayName("Deve filtrar por avaliação mínima")
    void deveFiltrarPorAvaliacaoMinima() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, null, null, null, 4.5, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).allMatch(e -> e.getAvaliacaoMedia().compareTo(new BigDecimal("4.5")) >= 0);
    }

    @Test
    @DisplayName("Deve filtrar com múltiplos critérios")
    void deveFiltrarComMultiplosCriterios() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, "São Paulo", null, null, 4.6, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(e ->
                e.getEndereco().getCidade().equals("São Paulo") &&
                        e.getAvaliacaoMedia().compareTo(new BigDecimal("4.6")) >= 0
        );
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum estabelecimento atende os filtros")
    void deveRetornarListaVaziaQuandoNenhumEstabelecimentoAtendeOsFiltros() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                "Inexistente", null, null, null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve ser case insensitive na busca por nome")
    void deveSerCaseInsensitiveNaBuscaPorNome() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                "SALÃO", null, null, null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).containsIgnoringCase("salão");
    }

    @Test
    @DisplayName("Deve ser case insensitive na busca por cidade")
    void deveSerCaseInsensitiveNaBuscaPorCidade() {
        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
                null, "são paulo", null, null, null, null
        );
        when(estabelecimentoRepositorio.buscarTodos()).thenReturn(estabelecimentosMock);

        List<Estabelecimento> resultado = buscarEstabelecimentosUseCase.executar(filtros);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).allMatch(e -> e.getEndereco().getCidade().equalsIgnoreCase("São Paulo"));
    }

    private Estabelecimento criarEstabelecimento(String nome, String cidade, String bairro, BigDecimal avaliacaoMediaDesejada) {
        Endereco endereco = Endereco.criar("Rua Test", "123", null, bairro, cidade, "SP", "01234567");
        Estabelecimento estabelecimento = Estabelecimento.criar(nome, endereco);

        double media = avaliacaoMediaDesejada.doubleValue();

        if (media == 4.5) {
            int[] estrelas = {4, 4, 4, 5, 5, 5};
            for (int estrela : estrelas) {
                UUID clienteId = UUID.randomUUID();
                var avaliacao = com.agendamento.dominio.entidades.Avaliacao.criarParaEstabelecimento(
                        clienteId, estabelecimento.getId(), estrela, "Comentário teste"
                );
                estabelecimento.adicionarAvaliacao(avaliacao);
            }
        } else if (media == 4.8) {
            int[] estrelas = {4, 5, 5, 5, 5};
            for (int estrela : estrelas) {
                UUID clienteId = UUID.randomUUID();
                var avaliacao = com.agendamento.dominio.entidades.Avaliacao.criarParaEstabelecimento(
                        clienteId, estabelecimento.getId(), estrela, "Comentário teste"
                );
                estabelecimento.adicionarAvaliacao(avaliacao);
            }
        } else if (media == 4.2) {
            int[] estrelas = {4, 4, 4, 4, 5};
            for (int estrela : estrelas) {
                UUID clienteId = UUID.randomUUID();
                var avaliacao = com.agendamento.dominio.entidades.Avaliacao.criarParaEstabelecimento(
                        clienteId, estabelecimento.getId(), estrela, "Comentário teste"
                );
                estabelecimento.adicionarAvaliacao(avaliacao);
            }
        } else if (media == 4.7) {
            int[] estrelas = {4, 4, 4, 5, 5, 5, 5, 5, 5, 5};
            for (int estrela : estrelas) {
                UUID clienteId = UUID.randomUUID();
                var avaliacao = com.agendamento.dominio.entidades.Avaliacao.criarParaEstabelecimento(
                        clienteId, estabelecimento.getId(), estrela, "Comentário teste"
                );
                estabelecimento.adicionarAvaliacao(avaliacao);
            }
        } else {
            int estrelas = (int) Math.round(media);
            for (int i = 0; i < 5; i++) {
                UUID clienteId = UUID.randomUUID();
                var avaliacao = com.agendamento.dominio.entidades.Avaliacao.criarParaEstabelecimento(
                        clienteId, estabelecimento.getId(), estrelas, "Comentário teste"
                );
                estabelecimento.adicionarAvaliacao(avaliacao);
            }
        }

        return estabelecimento;
    }
}
