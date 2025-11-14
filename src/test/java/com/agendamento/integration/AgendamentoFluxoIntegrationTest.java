package com.agendamento.integration;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.dominio.entidades.*;
import com.agendamento.infraestrutura.dto.AgendamentoDTO;
import com.agendamento.infraestrutura.dto.ClienteDTO;
import com.agendamento.infraestrutura.dto.EstabelecimentoDTO;
import com.agendamento.infraestrutura.dto.EnderecoDTO;
import com.agendamento.infraestrutura.dto.ServicoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Fluxo completo de agendamento - Teste de integração")
class AgendamentoFluxoIntegrationTest {

    @Autowired
    private CatalogoService catalogoService;


    @Test
    @DisplayName("Deve executar fluxo completo de agendamento com sucesso")
    void deveExecutarFluxoCompletoDeAgendamentoComSucesso() {
        EstabelecimentoDTO estabelecimentoDTO = new EstabelecimentoDTO(
                "Salão Integração",
                new EnderecoDTO("Rua Teste", "123", null, "Centro", "São Paulo", "SP", "01234567"),
                "Salão para teste de integração",
                List.of("foto1.jpg"),
                List.of()
        );

        Estabelecimento estabelecimento = catalogoService.criarEstabelecimento(estabelecimentoDTO);
        assertThat(estabelecimento).isNotNull();
        assertThat(estabelecimento.getId()).isNotNull();

        ServicoDTO servicoDTO = new ServicoDTO(
                "Corte Integração",
                "Corte para teste",
                "Cabelo",
                50.0,
                60
        );

        Servico servico = catalogoService.criarServico(servicoDTO);
        assertThat(servico).isNotNull();
        assertThat(servico.getId()).isNotNull();

        ClienteDTO clienteDTO = new ClienteDTO(
                "Cliente Teste",
                "cliente@teste.com",
                "+5511999999999",
                new EnderecoDTO("Rua Cliente", "456", null, "Centro", "São Paulo", "SP", "01234567")
        );
        Cliente clienteSalvo = catalogoService.criarCliente(clienteDTO);
        assertThat(clienteSalvo).isNotNull();

        UUID profissionalId = UUID.randomUUID();

        Instant inicio = Instant.now().plus(2, ChronoUnit.HOURS);
        AgendamentoDTO agendamentoDTO = new AgendamentoDTO(
                clienteSalvo.getId(),
                estabelecimento.getId(),
                profissionalId,
                servico.getId(),
                inicio
        );
        assertThat(agendamentoDTO.clienteId()).isEqualTo(clienteSalvo.getId());
        assertThat(agendamentoDTO.estabelecimentoId()).isEqualTo(estabelecimento.getId());
        assertThat(agendamentoDTO.servicoId()).isEqualTo(servico.getId());
        assertThat(agendamentoDTO.inicio()).isEqualTo(inicio);
    }

    @Test
    @DisplayName("Deve buscar estabelecimentos com filtros")
    void deveBuscarEstabelecimentosComFiltros() {
        EstabelecimentoDTO estabelecimento1 = new EstabelecimentoDTO(
                "Salão Beleza SP",
                new EnderecoDTO("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567"),
                "Salão em São Paulo",
                List.of("foto1.jpg"),
                List.of()
        );

        EstabelecimentoDTO estabelecimento2 = new EstabelecimentoDTO(
                "Studio Hair RJ",
                new EnderecoDTO("Rua B", "456", null, "Copacabana", "Rio de Janeiro", "RJ", "12345678"),
                "Studio no Rio de Janeiro",
                List.of("foto2.jpg"),
                List.of()
        );

        catalogoService.criarEstabelecimento(estabelecimento1);
        catalogoService.criarEstabelecimento(estabelecimento2);

        List<Estabelecimento> todos = catalogoService.listarEstabelecimentos();
        assertThat(todos).hasSizeGreaterThanOrEqualTo(2);

        assertThat(todos).anyMatch(e -> e.getNome().contains("Salão Beleza SP"));
        assertThat(todos).anyMatch(e -> e.getNome().contains("Studio Hair RJ"));
    }

    @Test
    @DisplayName("Deve criar e gerenciar ciclo de vida de serviços")
    void deveCriarEGerenciarCicloDeVidaDeServicos() {
        ServicoDTO corte = new ServicoDTO("Corte Masculino", "Corte tradicional", "Cabelo", 30.0, 45);
        ServicoDTO manicure = new ServicoDTO("Manicure", "Cuidado das unhas", "Unha", 25.0, 60);
        ServicoDTO limpeza = new ServicoDTO("Limpeza de Pele", "Tratamento facial", "Estética", 80.0, 90);

        Servico servicoCorte = catalogoService.criarServico(corte);
        Servico servicoManicure = catalogoService.criarServico(manicure);
        Servico servicoLimpeza = catalogoService.criarServico(limpeza);

        assertThat(servicoCorte.getCategoria()).isEqualTo("Cabelo");
        assertThat(servicoManicure.getCategoria()).isEqualTo("Unha");
        assertThat(servicoLimpeza.getCategoria()).isEqualTo("Estética");

        Servico buscado = catalogoService.obterServico(servicoCorte.getId());
        assertThat(buscado.getNome()).isEqualTo("Corte Masculino");
        assertThat(buscado.getPreco()).isEqualTo(30.0);

        List<String> categorias = catalogoService.listarCategoriasServicos();
        assertThat(categorias).contains("Cabelo", "Unha", "Estética");
    }

    @Test
    @DisplayName("Deve avaliar estabelecimento")
    void deveAvaliarEstabelecimento() {
        EstabelecimentoDTO estabelecimentoDTO = new EstabelecimentoDTO(
                "Salão para Avaliação",
                new EnderecoDTO("Rua Avaliação", "789", null, "Centro", "São Paulo", "SP", "01234567"),
                "Descrição do salão",
                List.of("foto.jpg"),
                List.of()
        );

        Estabelecimento estabelecimento = catalogoService.criarEstabelecimento(estabelecimentoDTO);

        ClienteDTO clienteDTO = new ClienteDTO(
                "Cliente Avaliador",
                "avaliador@teste.com",
                "+5511888888888",
                new EnderecoDTO("Rua Cliente", "456", null, "Centro", "São Paulo", "SP", "01234567")
        );
        Cliente clienteSalvo = catalogoService.criarCliente(clienteDTO);

        Estabelecimento avaliado = catalogoService.avaliarEstabelecimento(
                estabelecimento.getId(),
                clienteSalvo.getId(),
                5,
                "Excelente atendimento!"
        );

        assertThat(avaliado).isNotNull();
        assertThat(avaliado.getAvaliacoes()).isNotEmpty();
        assertThat(avaliado.getAvaliacoes().get(0).getEstrelas()).isEqualTo(5);
        assertThat(avaliado.getAvaliacoes().get(0).getComentario()).isEqualTo("Excelente atendimento!");
    }
}
