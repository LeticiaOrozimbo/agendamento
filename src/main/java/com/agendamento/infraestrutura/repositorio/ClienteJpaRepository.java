package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClienteJpaRepository extends JpaRepository<Cliente, UUID> {
    boolean existsByEmail(String email);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    List<Cliente> findByEmailContainingIgnoreCase(String email);
}
