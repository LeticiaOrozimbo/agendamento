package com.agendamento.api;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.infraestrutura.dto.ServicoDTO;
import com.agendamento.infraestrutura.dto.response.ServicoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final CatalogoService catalogoService;

    public ServicoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@RequestBody ServicoDTO dto) {
        var servico = catalogoService.criarServico(dto);
        return ResponseEntity.ok(new ServicoResponseDTO(servico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody ServicoDTO dto) {
        var servico = catalogoService.atualizarServico(id, dto);
        return ResponseEntity.ok(new ServicoResponseDTO(servico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> obter(@PathVariable UUID id) {
        var servico = catalogoService.obterServico(id);
        return ResponseEntity.ok(new ServicoResponseDTO(servico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        catalogoService.excluirServico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precoMinimo,
            @RequestParam(required = false) Double precoMaximo) {
        return ResponseEntity.ok(
            catalogoService.listarServicos(categoria, precoMinimo, precoMaximo).stream()
                .map(ServicoResponseDTO::new)
                .toList()
        );
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        return ResponseEntity.ok(catalogoService.listarCategoriasServicos());
    }

    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<ServicoResponseDTO>> listarPorEstabelecimento(
            @PathVariable UUID estabelecimentoId) {
        return ResponseEntity.ok(
            catalogoService.listarServicosPorEstabelecimento(estabelecimentoId).stream()
                .map(ServicoResponseDTO::new)
                .toList()
        );
    }

    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<ServicoResponseDTO>> listarPorProfissional(
            @PathVariable UUID profissionalId) {
        return ResponseEntity.ok(
            catalogoService.listarServicosPorProfissional(profissionalId).stream()
                .map(ServicoResponseDTO::new)
                .toList()
        );
    }
}
