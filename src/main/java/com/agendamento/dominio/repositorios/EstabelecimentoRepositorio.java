package com.agendamento.dominio.repositorios;

import com.agendamento.dominio.entidades.Estabelecimento;
import java.util.List;

public interface EstabelecimentoRepositorio extends RepositorioBase<Estabelecimento> {
    List<Estabelecimento> buscarPorNome(String nome);
    List<Estabelecimento> buscarPorCidade(String cidade);
    List<Estabelecimento> buscarPorAvaliacao(double avaliacaoMinima);
    List<Estabelecimento> buscarPorServico(String servico);
}
