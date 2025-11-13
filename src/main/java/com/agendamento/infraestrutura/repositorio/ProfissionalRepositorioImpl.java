package com.agendamento.infraestrutura.repositorio;

import com.agendamento.dominio.entidades.Profissional;
import com.agendamento.dominio.repositorios.ProfissionalRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfissionalRepositorioImpl implements ProfissionalRepositorio {
    private final ProfissionalJpaRepository jpaRepository;

    public ProfissionalRepositorioImpl(ProfissionalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Profissional salvar(Profissional profissional) {
        return jpaRepository.save(profissional);
    }

    @Override
    public Optional<Profissional> buscarPorId(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Profissional> buscarTodos() {
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
    public List<Profissional> buscarPorNome(String nome) {
        return jpaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public List<Profissional> buscarPorEspecialidade(String especialidade) {
        return jpaRepository.findByEspecialidadesContainingIgnoreCase(especialidade);
    }

    @Override
    public List<Profissional> buscarPorServico(UUID servicoId) {
        return jpaRepository.findByServicoId(servicoId);
    }

    @Override
    public List<Profissional> buscarPorEstabelecimento(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId);
    }
}
