package com.agendamento.dominio.repositorios;

import com.agendamento.dominio.entidades.Profissional;
import java.util.List;
import java.util.UUID;

public interface ProfissionalRepositorio extends RepositorioBase<Profissional> {
    List<Profissional> buscarPorNome(String nome);
    List<Profissional> buscarPorEspecialidade(String especialidade);
    List<Profissional> buscarPorServico(UUID servicoId);
    List<Profissional> buscarPorEstabelecimento(UUID estabelecimentoId);
}
