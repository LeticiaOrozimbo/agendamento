package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EstabelecimentoRepositorioImpl implements EstabelecimentoRepositorio {
    private final EstabelecimentoJpaRepository jpaRepository;

    public EstabelecimentoRepositorioImpl(EstabelecimentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Estabelecimento salvar(Estabelecimento estabelecimento) {
        return jpaRepository.save(estabelecimento);
    }

    @Override
    public Optional<Estabelecimento> buscarPorId(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Estabelecimento> buscarTodos() {
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
    public List<Estabelecimento> buscarPorNome(String nome) {
        return jpaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public List<Estabelecimento> buscarPorCidade(String cidade) {
        return jpaRepository.findByEnderecoCidadeContainingIgnoreCase(cidade);
    }

    @Override
    public List<Estabelecimento> buscarPorAvaliacao(double avaliacaoMinima) {
        return jpaRepository.findByAvaliacaoMediaGreaterThanEqual(avaliacaoMinima);
    }

    @Override
    public List<Estabelecimento> buscarPorServico(String servico) {
        try {
            UUID servicoId = UUID.fromString(servico);
            return jpaRepository.findByServicoId(servicoId);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}
