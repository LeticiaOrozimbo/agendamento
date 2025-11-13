package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgendamentoJpaRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByClienteId(UUID clienteId);
    List<Agendamento> findByProfissionalId(UUID profissionalId);
    List<Agendamento> findByEstabelecimentoId(UUID estabelecimentoId);

    @Query("SELECT a FROM Agendamento a WHERE a.profissionalId = :profissionalId AND a.inicio < :fim AND a.fim > :inicio")
    List<Agendamento> findByProfissionalIdAndPeriodo(UUID profissionalId, Instant inicio, Instant fim);

    @Query("SELECT a FROM Agendamento a WHERE a.estabelecimentoId = :estabelecimentoId AND a.inicio < :fim AND a.fim > :inicio")
    List<Agendamento> findByEstabelecimentoIdAndPeriodo(UUID estabelecimentoId, Instant inicio, Instant fim);

    @Query("SELECT a FROM Agendamento a WHERE a.profissionalId = :profissionalId AND a.inicio < :fim AND a.fim > :inicio")
    List<Agendamento> findConflitosParaProfissional(UUID profissionalId, Instant inicio, Instant fim);

    @Query("SELECT a FROM Agendamento a WHERE a.inicio < :fim AND a.fim > :inicio")
    List<Agendamento> findByIntervalo(Instant inicio, Instant fim);

    List<Agendamento> findByStatus(Agendamento.Status status);
}
