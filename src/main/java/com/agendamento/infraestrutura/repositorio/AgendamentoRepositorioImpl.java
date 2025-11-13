package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Agendamento;
import com.agendamento.dominio.repositorios.AgendamentoRepositorio;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AgendamentoRepositorioImpl implements AgendamentoRepositorio {
    private final AgendamentoJpaRepository jpaRepository;

    public AgendamentoRepositorioImpl(AgendamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        return jpaRepository.save(agendamento);
    }

    @Override
    public Optional<Agendamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Agendamento> buscarTodos() {
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
    public List<Agendamento> buscarPorCliente(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Agendamento> buscarPorProfissional(UUID profissionalId) {
        return jpaRepository.findByProfissionalId(profissionalId);
    }

    @Override
    public List<Agendamento> buscarPorEstabelecimento(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId);
    }

    @Override
    public List<Agendamento> buscarPorIntervalo(Instant inicio, Instant fim) {
        return jpaRepository.findByIntervalo(inicio, fim);
    }

    @Override
    public List<Agendamento> buscarPorStatus(Agendamento.Status status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<Agendamento> buscarConflitosParaProfissional(UUID profissionalId, Instant inicio, Instant fim) {
        return jpaRepository.findConflitosParaProfissional(profissionalId, inicio, fim);
    }
}
