package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Servico;
import com.agendamento.dominio.repositorios.ServicoRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServicoRepositorioImpl implements ServicoRepositorio {
    private final ServicoJpaRepository jpaRepository;

    public ServicoRepositorioImpl(ServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Servico salvar(Servico servico) {
        return jpaRepository.save(servico);
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Servico> buscarTodos() {
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
    public List<Servico> buscarPorCategoria(String categoria) {
        return jpaRepository.findByCategoriaContainingIgnoreCase(categoria);
    }

    @Override
    public List<Servico> buscarPorFaixaPreco(double precoMinimo, double precoMaximo) {
        return jpaRepository.findByPrecoBetween(precoMinimo, precoMaximo);
    }

    @Override
    public List<String> listarCategorias() {
        return jpaRepository.findAllCategories();
    }
}
