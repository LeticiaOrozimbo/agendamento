package com.agendamento.api;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.dominio.entidades.Endereco;
import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.infraestrutura.dto.EnderecoDTO;
import com.agendamento.infraestrutura.dto.EstabelecimentoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstabelecimentoController.class)
@DisplayName("EstabelecimentoController - Testes de integração")
class EstabelecimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogoService catalogoService;

    @MockBean
    private com.agendamento.aplicacao.usecases.BuscarEstabelecimentosComFiltrosUseCase buscarEstabelecimentosUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("Deve criar estabelecimento com sucesso")
    void deveCriarEstabelecimentoComSucesso() throws Exception {
        EstabelecimentoDTO dto = new EstabelecimentoDTO(
                "Salão Beleza",
                new EnderecoDTO("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567"),
                "Salão completo de beleza",
                List.of("foto1.jpg"),
                List.of()
        );

        Estabelecimento estabelecimento = criarEstabelecimentoMock();

        when(catalogoService.criarEstabelecimento(any(EstabelecimentoDTO.class))).thenReturn(estabelecimento);

        mockMvc.perform(post("/api/estabelecimentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salão Test"))
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"));

        verify(catalogoService).criarEstabelecimento(any(EstabelecimentoDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 quando dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        EstabelecimentoDTO dto = new EstabelecimentoDTO(
                null,
                new EnderecoDTO("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567"),
                "Descrição",
                List.of(),
                List.of()
        );

        when(catalogoService.criarEstabelecimento(any(EstabelecimentoDTO.class)))
                .thenThrow(new RegraNegocioExcecao("Nome é obrigatório"));

        mockMvc.perform(post("/api/estabelecimentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve obter estabelecimento por ID")
    void deveObterEstabelecimentoPorId() throws Exception {
        UUID id = UUID.randomUUID();
        Estabelecimento estabelecimento = criarEstabelecimentoMock();
        when(catalogoService.obterEstabelecimento(id)).thenReturn(estabelecimento);

        mockMvc.perform(get("/api/estabelecimentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salão Test"))
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"));

        verify(catalogoService).obterEstabelecimento(id);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 quando estabelecimento não existe")
    void deveRetornar404QuandoEstabelecimentoNaoExiste() throws Exception {
        UUID id = UUID.randomUUID();
        when(catalogoService.obterEstabelecimento(id))
                .thenThrow(new RegraNegocioExcecao("Estabelecimento não encontrado"));

        mockMvc.perform(get("/api/estabelecimentos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve listar estabelecimentos")
    void deveListarEstabelecimentos() throws Exception {
        List<Estabelecimento> estabelecimentos = Arrays.asList(
                criarEstabelecimentoMock(),
                criarEstabelecimentoMock()
        );
        when(catalogoService.listarEstabelecimentos()).thenReturn(estabelecimentos);

        mockMvc.perform(get("/api/estabelecimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(catalogoService).listarEstabelecimentos();
    }

    @Test
    @WithMockUser
    @DisplayName("Deve avaliar estabelecimento com sucesso")
    void deveAvaliarEstabelecimentoComSucesso() throws Exception {
        UUID estabelecimentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        Estabelecimento estabelecimento = criarEstabelecimentoMock();

        when(catalogoService.avaliarEstabelecimento(estabelecimentoId, clienteId, 5, "Excelente!"))
                .thenReturn(estabelecimento);

        mockMvc.perform(post("/api/estabelecimentos/{id}/avaliacoes", estabelecimentoId)
                        .with(csrf())
                        .param("clienteId", clienteId.toString())
                        .param("estrelas", "5")
                        .param("comentario", "Excelente!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salão Test"));

        verify(catalogoService).avaliarEstabelecimento(estabelecimentoId, clienteId, 5, "Excelente!");
    }


    private Estabelecimento criarEstabelecimentoMock() {
        Endereco endereco = Endereco.criar("Rua A", "123", null, "Centro", "São Paulo", "SP", "01234567");
        Estabelecimento estabelecimento = Estabelecimento.criar("Salão Test", endereco);

        if (estabelecimento == null) {
            throw new RuntimeException("Falha ao criar estabelecimento mock");
        }

        return estabelecimento;
    }
}
