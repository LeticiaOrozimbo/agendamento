package com.agendamento.api;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.infraestrutura.dto.DisponibilidadeDTO;
import com.agendamento.infraestrutura.dto.ProfissionalDTO;
import com.agendamento.infraestrutura.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profissionais")
@RequiredArgsConstructor
public class ProfissionalController {
    private final CatalogoService catalogoService;

    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> criar(@RequestBody ProfissionalDTO dto) {
        var profissional = catalogoService.criarProfissional(dto);
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
            catalogoService.listarProfissionais().stream()
                .map(ProfissionalResponseDTO::new)
                .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody ProfissionalDTO dto) {
        var profissional = catalogoService.atualizarProfissional(id, dto);
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> obter(@PathVariable UUID id) {
        var profissional = catalogoService.obterProfissional(id);
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        catalogoService.excluirProfissional(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<ProfissionalResponseDTO> adicionarServico(
            @PathVariable UUID id,
            @RequestParam UUID servicoId) {
        var profissional = catalogoService.adicionarServicoProfissional(id, servicoId);
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @DeleteMapping("/{id}/servicos/{servicoId}")
    public ResponseEntity<Void> removerServico(
            @PathVariable UUID id,
            @PathVariable UUID servicoId) {
        catalogoService.removerServicoProfissional(id, servicoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/disponibilidade")
    public ResponseEntity<ProfissionalResponseDTO> definirDisponibilidade(
            @PathVariable UUID id,
            @RequestBody DisponibilidadeDTO dto) {
        var profissional = catalogoService.definirDisponibilidadeProfissional(
            id, dto.dia(), dto.inicio(), dto.fim()
        );
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @PostMapping("/{id}/avaliacoes")
    public ResponseEntity<ProfissionalResponseDTO> avaliar(
            @PathVariable UUID id,
            @RequestParam UUID clienteId,
            @RequestParam int estrelas,
            @RequestParam String comentario) {
        var profissional = catalogoService.avaliarProfissional(id, clienteId, estrelas, comentario);
        return ResponseEntity.ok(new ProfissionalResponseDTO(profissional));
    }

    @GetMapping("/{id}/agendamentos")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentos(
            @PathVariable UUID id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(
            catalogoService.listarAgendamentosProfissional(id, status).stream()
                .map(AgendamentoResponseDTO::new)
                .toList()
        );
    }
}
