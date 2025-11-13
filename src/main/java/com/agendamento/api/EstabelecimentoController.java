package com.agendamento.api;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.aplicacao.usecases.BuscarEstabelecimentosComFiltrosUseCase;
import com.agendamento.infraestrutura.dto.EstabelecimentoDTO;
import com.agendamento.infraestrutura.dto.response.EstabelecimentoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos")
public class EstabelecimentoController {
    private final CatalogoService catalogoService;
    private final BuscarEstabelecimentosComFiltrosUseCase buscarEstabelecimentosUseCase;

    public EstabelecimentoController(CatalogoService catalogoService,
                                   BuscarEstabelecimentosComFiltrosUseCase buscarEstabelecimentosUseCase) {
        this.catalogoService = catalogoService;
        this.buscarEstabelecimentosUseCase = buscarEstabelecimentosUseCase;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoResponseDTO> criar(@RequestBody EstabelecimentoDTO dto) {
        var estabelecimento = catalogoService.criarEstabelecimento(dto);
        return ResponseEntity.ok(new EstabelecimentoResponseDTO(estabelecimento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> obter(@PathVariable UUID id) {
        var estabelecimento = catalogoService.obterEstabelecimento(id);
        return ResponseEntity.ok(new EstabelecimentoResponseDTO(estabelecimento));
    }

    @GetMapping
    public ResponseEntity<List<EstabelecimentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
            catalogoService.listarEstabelecimentos().stream()
                .map(EstabelecimentoResponseDTO::new)
                .toList()
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EstabelecimentoResponseDTO>> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) String servico,
            @RequestParam(required = false) Double avaliacaoMinima,
            @RequestParam(required = false) Double precoMaximo) {

        var filtros = new BuscarEstabelecimentosComFiltrosUseCase.BuscaFiltros(
            nome, cidade, bairro, servico, avaliacaoMinima, precoMaximo
        );

        return ResponseEntity.ok(
            buscarEstabelecimentosUseCase.executar(filtros).stream()
                .map(EstabelecimentoResponseDTO::new)
                .toList()
        );
    }

    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<EstabelecimentoResponseDTO> avaliar(
            @PathVariable UUID id,
            @RequestParam UUID clienteId,
            @RequestParam int estrelas,
            @RequestParam String comentario) {
        var estabelecimento = catalogoService.avaliarEstabelecimento(id, clienteId, estrelas, comentario);
        return ResponseEntity.ok(new EstabelecimentoResponseDTO(estabelecimento));
    }
}
