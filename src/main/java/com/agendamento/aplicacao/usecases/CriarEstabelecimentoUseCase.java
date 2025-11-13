package com.agendamento.aplicacao.usecases;

import com.agendamento.dominio.entidades.Estabelecimento;
import com.agendamento.dominio.entidades.Endereco;
import com.agendamento.dominio.repositorios.EstabelecimentoRepositorio;
import com.agendamento.infraestrutura.dto.EstabelecimentoDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarEstabelecimentoUseCase {
    private final EstabelecimentoRepositorio estabelecimentoRepositorio;

    public CriarEstabelecimentoUseCase(EstabelecimentoRepositorio estabelecimentoRepositorio) {
        this.estabelecimentoRepositorio = estabelecimentoRepositorio;
    }

    @Transactional
    public Estabelecimento executar(EstabelecimentoDTO dto) {
        // Criar endereço
        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        // Criar estabelecimento
        var estabelecimento = Estabelecimento.criar(dto.nome(), endereco);

        // Configurar propriedades opcionais
        if (dto.descricao() != null) {
            estabelecimento.setDescricao(dto.descricao());
        }

        // Adicionar fotos se fornecidas
        if (dto.fotos() != null) {
            for (var foto : dto.fotos()) {
                estabelecimento.adicionarFoto(foto);
            }
        }

        // Definir horários de funcionamento se fornecidos
        if (dto.horariosFuncionamento() != null) {
            for (var horario : dto.horariosFuncionamento()) {
                estabelecimento.definirHorario(
                    horario.diaSemana(),
                    horario.horarioAbertura(),
                    horario.horarioFechamento()
                );
            }
        }

        return estabelecimentoRepositorio.salvar(estabelecimento);
    }
}
