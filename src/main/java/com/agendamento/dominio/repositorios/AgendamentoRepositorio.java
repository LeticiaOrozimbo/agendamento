package com.agendamento.dominio.repositorios;

import com.agendamento.dominio.entidades.Agendamento;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AgendamentoRepositorio extends RepositorioBase<Agendamento> {
    List<Agendamento> buscarPorCliente(UUID clienteId);
    List<Agendamento> buscarPorProfissional(UUID profissionalId);
    List<Agendamento> buscarPorEstabelecimento(UUID estabelecimentoId);
    List<Agendamento> buscarPorIntervalo(Instant inicio, Instant fim);
    List<Agendamento> buscarPorStatus(Agendamento.Status status);
    List<Agendamento> buscarConflitosParaProfissional(UUID profissionalId, Instant inicio, Instant fim);
}
