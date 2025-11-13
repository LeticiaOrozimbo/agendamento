package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuscarEstabelecimentosComFiltrosUseCase {
    private final EstabelecimentoRepositorio estabelecimentoRepositorio;

    public BuscarEstabelecimentosComFiltrosUseCase(EstabelecimentoRepositorio estabelecimentoRepositorio) {
        this.estabelecimentoRepositorio = estabelecimentoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> executar(BuscaFiltros filtros) {
        return estabelecimentoRepositorio.buscarTodos().stream()
            .filter(e -> filtros.nome() == null ||
                    e.getNome().toLowerCase().contains(filtros.nome().toLowerCase()))
            .filter(e -> filtros.cidade() == null ||
                    e.getEndereco().getCidade().toLowerCase().contains(filtros.cidade().toLowerCase()))
            .filter(e -> filtros.bairro() == null ||
                    e.getEndereco().getBairro().toLowerCase().contains(filtros.bairro().toLowerCase()))
            .filter(e -> filtros.avaliacaoMinima() == null ||
                    e.getAvaliacaoMedia().compareTo(BigDecimal.valueOf(filtros.avaliacaoMinima())) >= 0)
            .collect(Collectors.toList());
    }

    public record BuscaFiltros(
        String nome,
        String cidade,
        String bairro,
        String servico,
        Double avaliacaoMinima,
        Double precoMaximo
    ) {}
}
