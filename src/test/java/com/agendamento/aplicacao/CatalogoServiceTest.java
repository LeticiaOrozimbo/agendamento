package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.*;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.*;
import com.agendamento.infraestrutura.dto.EstabelecimentoDTO;
import com.agendamento.infraestrutura.dto.EnderecoDTO;
import com.agendamento.infraestrutura.dto.ServicoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogoService - Testes unitários")
class CatalogoServiceTest {

    @Mock
    private EstabelecimentoRepositorio estabelecimentoRepositorio;

    @Mock
    private ProfissionalRepositorio profissionalRepositorio;

    @Mock
    private ServicoRepositorio servicoRepositorio;

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @Mock
    private AgendamentoRepositorio agendamentoRepositorio;

    private CatalogoService catalogoService;

    @BeforeEach
    void setUp() {
        catalogoService = new CatalogoService(
                estabelecimentoRepositorio,
                profissionalRepositorio,
                servicoRepositorio,
                clienteRepositorio,
                agendamentoRepositorio
        );
    }

    @Test
    @DisplayName("Deve criar estabelecimento com sucesso")
    void deveCriarEstabelecimentoComSucesso() {
        EstabelecimentoDTO dto = new EstabelecimentoDTO(
                "Salão Beleza",
                new EnderecoDTO("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567"),
                "Descrição do salão",
                List.of("foto1.jpg"),
                List.of()
        );

        Estabelecimento estabelecimento = criarEstabelecimentoMock();
        when(estabelecimentoRepositorio.salvar(any(Estabelecimento.class))).thenReturn(estabelecimento);

        Estabelecimento resultado = catalogoService.criarEstabelecimento(dto);

        assertThat(resultado).isNotNull();
        verify(estabelecimentoRepositorio).salvar(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve obter estabelecimento por ID")
    void deveObterEstabelecimentoPorId() {
        UUID id = UUID.randomUUID();
        Estabelecimento estabelecimento = criarEstabelecimentoMock();
        when(estabelecimentoRepositorio.buscarPorId(id)).thenReturn(Optional.of(estabelecimento));

        Estabelecimento resultado = catalogoService.obterEstabelecimento(id);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(estabelecimento);
    }

    @Test
    @DisplayName("Deve lançar exceção quando estabelecimento não existe")
    void deveLancarExcecaoQuandoEstabelecimentoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(estabelecimentoRepositorio.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.obterEstabelecimento(id))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Estabelecimento não encontrado");
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void deveCriarServicoComSucesso() {
        ServicoDTO dto = new ServicoDTO(
                "Corte de Cabelo",
                "Corte moderno",
                "Cabelo",
                50.0,
                60
        );

        Servico servico = Servico.criar("Corte de Cabelo", "Corte moderno", "Cabelo", 50.0, 60);
        when(servicoRepositorio.salvar(any(Servico.class))).thenReturn(servico);

        Servico resultado = catalogoService.criarServico(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Corte de Cabelo");
        assertThat(resultado.getCategoria()).isEqualTo("Cabelo");
        assertThat(resultado.getPreco()).isEqualTo(50.0);
        verify(servicoRepositorio).salvar(any(Servico.class));
    }

    @Test
    @DisplayName("Deve obter serviço por ID")
    void deveObterServicoPorId() {
        UUID id = UUID.randomUUID();
        Servico servico = Servico.criar("Corte", "Descrição", "Categoria", 50.0, 60);
        when(servicoRepositorio.buscarPorId(id)).thenReturn(Optional.of(servico));

        Servico resultado = catalogoService.obterServico(id);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(servico);
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não existe")
    void deveLancarExcecaoQuandoServicoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(servicoRepositorio.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.obterServico(id))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Serviço não encontrado");
    }



    @Test
    @DisplayName("Deve listar todas as categorias de serviços")
    void deveListarTodasAsCategorias() {
        List<String> categorias = List.of("Cabelo", "Unha", "Estética");
        when(servicoRepositorio.listarCategorias()).thenReturn(categorias);

        List<String> resultado = catalogoService.listarCategoriasServicos();

        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactlyInAnyOrder("Cabelo", "Unha", "Estética");
    }

    @Test
    @DisplayName("Deve avaliar estabelecimento com sucesso")
    void deveAvaliarEstabelecimentoComSucesso() {
        UUID estabelecimentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        int estrelas = 5;
        String comentario = "Excelente!";

        Estabelecimento estabelecimento = criarEstabelecimentoMock();
        Cliente cliente = criarClienteMock();

        when(estabelecimentoRepositorio.buscarPorId(estabelecimentoId)).thenReturn(Optional.of(estabelecimento));
        when(estabelecimentoRepositorio.salvar(any(Estabelecimento.class))).thenReturn(estabelecimento);

        Estabelecimento resultado = catalogoService.avaliarEstabelecimento(estabelecimentoId, clienteId, estrelas, comentario);

        assertThat(resultado).isNotNull();
        verify(estabelecimentoRepositorio).salvar(estabelecimento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao avaliar estabelecimento inexistente")
    void deveLancarExcecaoAoAvaliarEstabelecimentoInexistente() {
        UUID estabelecimentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        when(estabelecimentoRepositorio.buscarPorId(estabelecimentoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.avaliarEstabelecimento(estabelecimentoId, clienteId, 5, "Comentário"))
                .isInstanceOf(RegraNegocioExcecao.class)
                .hasMessage("Estabelecimento não encontrado");
    }


    private Estabelecimento criarEstabelecimentoMock() {
        Endereco endereco = Endereco.criar("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567");
        return Estabelecimento.criar("Salão Test", endereco);
    }

    private Cliente criarClienteMock() {
        Endereco endereco = Endereco.criar("Rua B", "456", null, "Centro", "São Paulo", "SP", "01234567");
        return Cliente.criar(
                "João Silva",
                "joao@email.com",
                "+5511999999999",
                endereco
        );
    }
}
