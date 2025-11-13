package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Servico;
import com.agendamento.dominio.repositorios.ServicoRepositorio;
import com.agendamento.infraestrutura.dto.ServicoDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarServicoUseCase {
    private final ServicoRepositorio servicoRepositorio;

    public CriarServicoUseCase(ServicoRepositorio servicoRepositorio) {
        this.servicoRepositorio = servicoRepositorio;
    }

    @Transactional
    public Servico executar(ServicoDTO dto) {
        var servico = Servico.criar(
            dto.nome(),
            dto.descricao(),
            dto.categoria(),
            dto.preco(),
            dto.duracaoMinutos()
        );

        return servicoRepositorio.salvar(servico);
    }
}
