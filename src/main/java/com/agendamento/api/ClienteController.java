package com.agendamento.api;

import com.agendamento.aplicacao.CatalogoService;
import com.agendamento.infraestrutura.dto.ClienteDTO;
import com.agendamento.infraestrutura.dto.response.AgendamentoResponseDTO;
import com.agendamento.infraestrutura.dto.response.ClienteResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final CatalogoService catalogoService;

    public ClienteController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody ClienteDTO dto) {
        var cliente = catalogoService.criarCliente(dto);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody ClienteDTO dto) {
        var cliente = catalogoService.atualizarCliente(id, dto);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                catalogoService.listarClientes().stream()
                        .map(ClienteResponseDTO::new)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obter(@PathVariable UUID id) {
        var cliente = catalogoService.obterCliente(id);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        catalogoService.excluirCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/agendamentos")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentos(
            @PathVariable UUID id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(
                catalogoService.listarAgendamentosCliente(id, status).stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList()
        );
    }

    @PostMapping("/{id}/calendario")
    public ResponseEntity<ClienteResponseDTO> integrarCalendario(
            @PathVariable UUID id,
            @RequestParam String email) {
        var cliente = catalogoService.integrarCalendarioCliente(id, email);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));
    }

    @DeleteMapping("/{id}/calendario")
    public ResponseEntity<Void> removerIntegracaoCalendario(@PathVariable UUID id) {
        catalogoService.removerIntegracaoCalendarioCliente(id);
        return ResponseEntity.noContent().build();
    }

}
