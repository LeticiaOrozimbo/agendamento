package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServicoJpaRepository extends JpaRepository<Servico, UUID> {
    List<Servico> findByCategoriaContainingIgnoreCase(String categoria);

    @Query("SELECT s FROM Servico s WHERE s.preco BETWEEN :precoMinimo AND :precoMaximo")
    List<Servico> findByPrecoBetween(double precoMinimo, double precoMaximo);

    @Query("SELECT DISTINCT s.categoria FROM Servico s ORDER BY s.categoria")
    List<String> findAllCategories();
}
