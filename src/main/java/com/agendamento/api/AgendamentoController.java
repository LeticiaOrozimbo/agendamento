package com.agendamento.api;

import com.agendamento.aplicacao.usecases.*;
import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.infraestrutura.dto.AgendamentoDTO;
import com.agendamento.infraestrutura.dto.response.AgendamentoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {
    private final CriarAgendamentoUseCase criarAgendamentoUseCase;
    private final CancelarAgendamentoUseCase cancelarAgendamentoUseCase;
    private final ReagendarAgendamentoUseCase reagendarAgendamentoUseCase;
    private final BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase;
    private final ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase;
    private final ConcluirAgendamentoUseCase concluirAgendamentoUseCase;
    private final BuscarAgendamentosPorStatusUseCase buscarAgendamentosPorStatusUseCase;
    private final ListarAgendamentosAtivosUseCase listarAgendamentosAtivosUseCase;

    public AgendamentoController(CriarAgendamentoUseCase criarAgendamentoUseCase,
                                 CancelarAgendamentoUseCase cancelarAgendamentoUseCase,
                                 ReagendarAgendamentoUseCase reagendarAgendamentoUseCase,

                                 BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase,
                                 ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase,
                                 ConcluirAgendamentoUseCase concluirAgendamentoUseCase,
                                 BuscarAgendamentosPorStatusUseCase buscarAgendamentosPorStatusUseCase,
                                 ListarAgendamentosAtivosUseCase listarAgendamentosAtivosUseCase) {
        this.criarAgendamentoUseCase = criarAgendamentoUseCase;
        this.cancelarAgendamentoUseCase = cancelarAgendamentoUseCase;
        this.reagendarAgendamentoUseCase = reagendarAgendamentoUseCase;

        this.buscarAgendamentoPorIdUseCase = buscarAgendamentoPorIdUseCase;
        this.confirmarAgendamentoUseCase = confirmarAgendamentoUseCase;
        this.concluirAgendamentoUseCase = concluirAgendamentoUseCase;
        this.buscarAgendamentosPorStatusUseCase = buscarAgendamentosPorStatusUseCase;
        this.listarAgendamentosAtivosUseCase = listarAgendamentosAtivosUseCase;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@RequestBody AgendamentoDTO dto) {
        var agendamento = criarAgendamentoUseCase.executar(dto);
        return ResponseEntity.ok(new AgendamentoResponseDTO(agendamento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable UUID id) {
        var agendamento = buscarAgendamentoPorIdUseCase.executar(id);
        return ResponseEntity.ok(new AgendamentoResponseDTO(agendamento));
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        cancelarAgendamentoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable UUID id) {
        confirmarAgendamentoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable UUID id) {
        concluirAgendamentoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reagendar")
    public ResponseEntity<Void> reagendar(@PathVariable UUID id, @RequestBody AgendamentoDTO dto) {
        // Assumindo duração padrão de 60 minutos para o exemplo
        var fim = dto.inicio().plus(Duration.ofMinutes(60));
        reagendarAgendamentoUseCase.executar(id, dto.inicio(), fim);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profissional/{id}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorProfissional(@PathVariable UUID id) {
        return ResponseEntity.ok(
                listarAgendamentosAtivosUseCase.porProfissional(id).stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList()
        );
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorCliente(@PathVariable UUID id) {
        return ResponseEntity.ok(
                listarAgendamentosAtivosUseCase.porCliente(id).stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList()
        );
    }

    @GetMapping("/estabelecimento/{id}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorEstabelecimento(@PathVariable UUID id) {
        return ResponseEntity.ok(
                listarAgendamentosAtivosUseCase.porEstabelecimento(id).stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList()
        );
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                listarAgendamentosAtivosUseCase.todos().stream()
                        .map(AgendamentoResponseDTO::new)
                        .toList()
        );
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorStatus(@PathVariable String status) {
        try {
            var statusEnum = Agendamento.Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(
                    buscarAgendamentosPorStatusUseCase.executar(statusEnum).stream()
                            .map(AgendamentoResponseDTO::new)
                            .toList()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
