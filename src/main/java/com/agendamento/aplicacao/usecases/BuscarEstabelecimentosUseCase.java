package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BuscarEstabelecimentosUseCase {
    private final EstabelecimentoRepositorio estabelecimentoRepositorio;

    public BuscarEstabelecimentosUseCase(EstabelecimentoRepositorio estabelecimentoRepositorio) {
        this.estabelecimentoRepositorio = estabelecimentoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> porNome(String nome) {
        return estabelecimentoRepositorio.buscarPorNome(nome);
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> porLocalizacao(String cidade, String bairro) {
        return estabelecimentoRepositorio.buscarTodos().stream()
            .filter(e -> (cidade == null || e.getEndereco().getCidade().toLowerCase().contains(cidade.toLowerCase()))
                     && (bairro == null || e.getEndereco().getBairro().toLowerCase().contains(bairro.toLowerCase())))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> porAvaliacao(Double avaliacaoMinima) {
        return estabelecimentoRepositorio.buscarTodos().stream()
            .filter(e -> e.getAvaliacaoMedia().compareTo(BigDecimal.valueOf(avaliacaoMinima)) >= 0)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> todos() {
        return estabelecimentoRepositorio.buscarTodos();
    }
}
