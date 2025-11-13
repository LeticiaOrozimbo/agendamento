package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Avaliacao;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import com.agendamento.dominio.repositorios.ProfissionalRepositorio;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AvaliarServicoUseCase {
    private final EstabelecimentoRepositorio estabelecimentoRepositorio;
    private final ProfissionalRepositorio profissionalRepositorio;

    public AvaliarServicoUseCase(EstabelecimentoRepositorio estabelecimentoRepositorio,
                               ProfissionalRepositorio profissionalRepositorio) {
        this.estabelecimentoRepositorio = estabelecimentoRepositorio;
        this.profissionalRepositorio = profissionalRepositorio;
    }

    @Transactional
    public void executar(UUID estabelecimentoId, UUID profissionalId, UUID clienteId,
                        int estrelas, String comentario) {

        // Validar parâmetros
        if (estrelas < 1 || estrelas > 5) {
            throw new RegraNegocioExcecao("Avaliação deve ser entre 1 e 5 estrelas");
        }

        // Criar avaliação
        var avaliacao = Avaliacao.criar(clienteId, estrelas, comentario);

        // Adicionar ao estabelecimento se fornecido
        if (estabelecimentoId != null) {
            var estabelecimento = estabelecimentoRepositorio.buscarPorId(estabelecimentoId)
                .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));

            estabelecimento.adicionarAvaliacao(avaliacao);
            estabelecimentoRepositorio.salvar(estabelecimento);
        }

        // Adicionar ao profissional se fornecido
        if (profissionalId != null) {
            var profissional = profissionalRepositorio.buscarPorId(profissionalId)
                .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

            profissional.adicionarAvaliacao(avaliacao);
            profissionalRepositorio.salvar(profissional);
        }
    }
}
