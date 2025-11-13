package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstabelecimentoJpaRepository extends JpaRepository<Estabelecimento, UUID> {
    List<Estabelecimento> findByNomeContainingIgnoreCase(String nome);
    List<Estabelecimento> findByEnderecoCidadeContainingIgnoreCase(String cidade);

    @Query("SELECT e FROM Estabelecimento e WHERE e.avaliacaoMedia >= :avaliacaoMinima")
    List<Estabelecimento> findByAvaliacaoMediaGreaterThanEqual(double avaliacaoMinima);

    @Query("SELECT DISTINCT e FROM Estabelecimento e JOIN e.servicosIds s WHERE s = :servicoId")
    List<Estabelecimento> findByServicoId(UUID servicoId);
}
