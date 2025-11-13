package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Cliente;
import com.agendamento.dominio.repositorios.ClienteRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClienteRepositorioImpl implements ClienteRepositorio {
    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositorioImpl(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        return jpaRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll();
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existe(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean emailJaCadastrado(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<Cliente> buscarPorNome(String nome) {
        return jpaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public List<Cliente> buscarPorEmail(String email) {
        return jpaRepository.findByEmailContainingIgnoreCase(email);
    }
}
