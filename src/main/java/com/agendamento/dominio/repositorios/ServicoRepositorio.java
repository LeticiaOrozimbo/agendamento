package com.agendamento.dominio.repositorios;

import com.agendamento.dominio.entidades.Servico;
import java.util.List;
import java.util.UUID;

public interface ServicoRepositorio extends RepositorioBase<Servico> {
    List<Servico> buscarPorCategoria(String categoria);
    List<Servico> buscarPorFaixaPreco(double precoMinimo, double precoMaximo);
    List<String> listarCategorias();
//    List<Servico> buscarPorEstabelecimento(UUID estabelecimentoId);
//    List<Servico> buscarPorProfissional(UUID profissionalId);
}
