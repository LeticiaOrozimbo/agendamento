package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfissionalJpaRepository extends JpaRepository<Profissional, UUID> {
    List<Profissional> findByNomeContainingIgnoreCase(String nome);

    List<Profissional> findByEspecialidadesContainingIgnoreCase(String especialidade);

    @Query("SELECT p FROM Profissional p JOIN p.servicosIds s WHERE s = :servicoId")
    List<Profissional> findByServicoId(UUID servicoId);

    @Query("SELECT p FROM Profissional p WHERE p.id IN (SELECT ps FROM Estabelecimento e JOIN e.profissionaisIds ps WHERE e.id = :estabelecimentoId)")
    List<Profissional> findByEstabelecimentoId(UUID estabelecimentoId);
}

