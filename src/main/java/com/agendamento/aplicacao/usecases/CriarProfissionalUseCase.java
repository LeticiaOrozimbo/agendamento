package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Profissional;
import com.agendamento.dominio.repositorios.ProfissionalRepositorio;
import com.agendamento.infraestrutura.dto.ProfissionalDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarProfissionalUseCase {
    private final ProfissionalRepositorio profissionalRepositorio;

    public CriarProfissionalUseCase(ProfissionalRepositorio profissionalRepositorio) {
        this.profissionalRepositorio = profissionalRepositorio;
    }

    @Transactional
    public Profissional executar(ProfissionalDTO dto) {
        // Criar profissional com apenas o nome
        var profissional = Profissional.criar(dto.nome());

        // Definir propriedades opcionais
        if (dto.especialidades() != null) {
            profissional.setEspecialidades(dto.especialidades());
        }

        if (dto.tarifaBase() > 0) {
            profissional.setTarifaBase(dto.tarifaBase());
        }

        if (dto.foto() != null) {
            profissional.definirFoto(dto.foto());
        }

        // Adicionar disponibilidade se fornecida
        if (dto.disponibilidade() != null) {
            for (var disp : dto.disponibilidade()) {
                profissional.adicionarDisponibilidade(
                    disp.diaSemana(),
                    disp.horarioInicio(),
                    disp.horarioFim()
                );
            }
        }

        // Adicionar serviços se fornecidos
        if (dto.servicosIds() != null) {
            for (var servicoId : dto.servicosIds()) {
                profissional.adicionarServico(servicoId);
            }
        }

        return profissionalRepositorio.salvar(profissional);
    }
}
