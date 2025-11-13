package com.agendamento.aplicacao;

import com.agendamento.dominio.entidades.*;
import com.agendamento.dominio.excecao.RegraNegocioExcecao;
import com.agendamento.dominio.repositorios.*;
import com.agendamento.infraestrutura.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class CatalogoService {
    private final EstabelecimentoRepositorio estabelecimentoRepositorio;
    private final ProfissionalRepositorio profissionalRepositorio;
    private final ServicoRepositorio servicoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final AgendamentoRepositorio agendamentoRepositorio;

    public CatalogoService(EstabelecimentoRepositorio estabelecimentoRepositorio,
                          ProfissionalRepositorio profissionalRepositorio,
                          ServicoRepositorio servicoRepositorio,
                          ClienteRepositorio clienteRepositorio,
                          AgendamentoRepositorio agendamentoRepositorio) {
        this.estabelecimentoRepositorio = estabelecimentoRepositorio;
        this.profissionalRepositorio = profissionalRepositorio;
        this.servicoRepositorio = servicoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.agendamentoRepositorio = agendamentoRepositorio;
    }

    @Transactional
    public Estabelecimento criarEstabelecimento(EstabelecimentoDTO dto) {
        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        var estabelecimento = Estabelecimento.criar(dto.nome(), endereco);
        estabelecimento.setDescricao(dto.descricao());

        if (dto.fotos() != null) {
            dto.fotos().forEach(estabelecimento::adicionarFoto);
        }

        if (dto.horariosFuncionamento() != null) {
            dto.horariosFuncionamento().forEach(h ->
                estabelecimento.definirHorario(h.diaSemana(), h.horarioAbertura(), h.horarioFechamento())
            );
        }

        return estabelecimentoRepositorio.salvar(estabelecimento);
    }

    @Transactional
    public Profissional criarProfissional(ProfissionalDTO dto) {
        var profissional = Profissional.criar(dto.nome());
        profissional.setEspecialidades(dto.especialidades());
        profissional.definirFoto(dto.foto());
        profissional.setTarifaBase(dto.tarifaBase());

        if (dto.servicosIds() != null) {
            dto.servicosIds().forEach(profissional::adicionarServico);
        }

        if (dto.disponibilidade() != null) {
            dto.disponibilidade().forEach(d ->
                profissional.adicionarDisponibilidade(d.diaSemana(), d.horarioInicio(), d.horarioFim())
            );
        }

        return profissionalRepositorio.salvar(profissional);
    }

    @Transactional
    public Servico criarServico(ServicoDTO dto) {
        var servico = Servico.criar(
            dto.nome(),
            dto.descricao(),
            dto.categoria(),
            dto.preco(),
            dto.duracaoMinutos()
        );
        return servicoRepositorio.salvar(servico);
    }

    @Transactional
    public Cliente criarCliente(ClienteDTO dto) {
        if (clienteRepositorio.emailJaCadastrado(dto.email())) {
            throw new RegraNegocioExcecao("Email já cadastrado");
        }

        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        var cliente = Cliente.criar(
            dto.nome(),
            dto.email(),
            dto.telefone(),
            endereco
        );

        return clienteRepositorio.salvar(cliente);
    }

    @Transactional
    public Estabelecimento avaliarEstabelecimento(UUID id, UUID clienteId, int estrelas, String comentario) {
        var estabelecimento = estabelecimentoRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));

        var avaliacao = Avaliacao.criarParaEstabelecimento(clienteId, id, estrelas, comentario);
        estabelecimento.adicionarAvaliacao(avaliacao);

        return estabelecimentoRepositorio.salvar(estabelecimento);
    }

    @Transactional
    public Profissional definirDisponibilidadeProfissional(UUID id, DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        var profissional = profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        profissional.adicionarDisponibilidade(dia, inicio, fim);
        return profissionalRepositorio.salvar(profissional);
    }

    @Transactional
    public Profissional avaliarProfissional(UUID id, UUID clienteId, int estrelas, String comentario) {
        var profissional = profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        var avaliacao = Avaliacao.criarParaProfissional(clienteId, id, estrelas, comentario);
        profissional.adicionarAvaliacao(avaliacao);

        return profissionalRepositorio.salvar(profissional);
    }

    @Transactional(readOnly = true)
    public Estabelecimento obterEstabelecimento(UUID id) {
        return estabelecimentoRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));
    }

    @Transactional(readOnly = true)
    public Profissional obterProfissional(UUID id) {
        return profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));
    }

    @Transactional(readOnly = true)
    public Servico obterServico(UUID id) {
        return servicoRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Serviço não encontrado"));
    }

    @Transactional(readOnly = true)
    public Cliente obterCliente(UUID id) {
        return clienteRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Cliente não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepositorio.buscarTodos();
    }

    @Transactional(readOnly = true)
    public List<Estabelecimento> listarEstabelecimentos() {
        return estabelecimentoRepositorio.buscarTodos();
    }

    @Transactional(readOnly = true)
    public List<Profissional> listarProfissionais() {
        return profissionalRepositorio.buscarTodos();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarServicos() {
        return servicoRepositorio.buscarTodos();
    }

    @Transactional(readOnly = true)
    public List<String> listarCategoriasServicos() {
        return servicoRepositorio.listarCategorias();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarServicos(String categoria, Double precoMinimo, Double precoMaximo) {
        if (categoria != null) {
            return servicoRepositorio.buscarPorCategoria(categoria);
        }
        if (precoMinimo != null && precoMaximo != null) {
            return servicoRepositorio.buscarPorFaixaPreco(precoMinimo, precoMaximo);
        }
        return servicoRepositorio.buscarTodos();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarServicosPorEstabelecimento(UUID estabelecimentoId) {
        var estabelecimento = estabelecimentoRepositorio.buscarPorId(estabelecimentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));

        return estabelecimento.getServicosIds().stream()
            .map(this::obterServico)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Profissional> listarProfissionaisPorEstabelecimento(UUID estabelecimentoId) {
        var estabelecimento = estabelecimentoRepositorio.buscarPorId(estabelecimentoId)
            .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));

        return estabelecimento.getProfissionaisIds().stream()
            .map(this::obterProfissional)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarServicosPorProfissional(UUID profissionalId) {
        var profissional = profissionalRepositorio.buscarPorId(profissionalId)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        return profissional.getServicosIds().stream()
            .map(this::obterServico)
            .toList();
    }

    @Transactional
    public void excluirEstabelecimento(UUID id) {
        if (!estabelecimentoRepositorio.existe(id)) {
            throw new RegraNegocioExcecao("Estabelecimento não encontrado");
        }
        estabelecimentoRepositorio.excluir(id);
    }

    @Transactional
    public void excluirProfissional(UUID id) {
        if (!profissionalRepositorio.existe(id)) {
            throw new RegraNegocioExcecao("Profissional não encontrado");
        }
        profissionalRepositorio.excluir(id);
    }

    @Transactional
    public void excluirServico(UUID id) {
        if (!servicoRepositorio.existe(id)) {
            throw new RegraNegocioExcecao("Serviço não encontrado");
        }
        servicoRepositorio.excluir(id);
    }

    @Transactional
    public void excluirCliente(UUID id) {
        if (!clienteRepositorio.existe(id)) {
            throw new RegraNegocioExcecao("Cliente não encontrado");
        }
        clienteRepositorio.excluir(id);
    }

    @Transactional
    public Estabelecimento atualizarEstabelecimento(UUID id, EstabelecimentoDTO dto) {
        var estabelecimento = estabelecimentoRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Estabelecimento não encontrado"));

        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        estabelecimento.setNome(dto.nome());
        estabelecimento.setEndereco(endereco);
        estabelecimento.setDescricao(dto.descricao());

        return estabelecimentoRepositorio.salvar(estabelecimento);
    }

    @Transactional
    public Profissional atualizarProfissional(UUID id, ProfissionalDTO dto) {
        var profissional = profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        profissional.setNome(dto.nome());
        profissional.setEspecialidades(dto.especialidades());
        profissional.definirFoto(dto.foto());
        profissional.setTarifaBase(dto.tarifaBase());

        return profissionalRepositorio.salvar(profissional);
    }

    @Transactional
    public Servico atualizarServico(UUID id, ServicoDTO dto) {
        var servico = servicoRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Serviço não encontrado"));

        servico.setNome(dto.nome());
        servico.setDescricao(dto.descricao());
        servico.setCategoria(dto.categoria());
        servico.setPreco(dto.preco());
        servico.setDuracaoMinutos(dto.duracaoMinutos());

        return servicoRepositorio.salvar(servico);
    }

    @Transactional
    public Cliente atualizarCliente(UUID id, ClienteDTO dto) {
        var cliente = clienteRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Cliente não encontrado"));

        var endereco = Endereco.criar(
            dto.endereco().logradouro(),
            dto.endereco().numero(),
            dto.endereco().complemento(),
            dto.endereco().bairro(),
            dto.endereco().cidade(),
            dto.endereco().estado(),
            dto.endereco().cep()
        );

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(endereco);

        return clienteRepositorio.salvar(cliente);
    }

    @Transactional
    public Cliente integrarCalendarioCliente(UUID id, String email) {
        var cliente = clienteRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Cliente não encontrado"));

        cliente.integrarCalendario(email);
        return clienteRepositorio.salvar(cliente);
    }

    @Transactional
    public void removerIntegracaoCalendarioCliente(UUID id) {
        var cliente = clienteRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Cliente não encontrado"));

        cliente.removerIntegracaoCalendario();
        clienteRepositorio.salvar(cliente);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarAgendamentosCliente(UUID clienteId, String status) {
        var agendamentos = agendamentoRepositorio.buscarPorCliente(clienteId);

        if (status != null && !status.isEmpty()) {
            try {
                Agendamento.Status statusEnum = Agendamento.Status.valueOf(status.toUpperCase());
                return agendamentos.stream()
                    .filter(a -> a.getStatus() == statusEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                throw new RegraNegocioExcecao("Status inválido: " + status);
            }
        }

        return agendamentos;
    }

    @Transactional
    public Profissional adicionarServicoProfissional(UUID id, UUID servicoId) {
        var profissional = profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        if (!servicoRepositorio.existe(servicoId)) {
            throw new RegraNegocioExcecao("Serviço não encontrado");
        }

        profissional.adicionarServico(servicoId);
        return profissionalRepositorio.salvar(profissional);
    }

    @Transactional
    public void removerServicoProfissional(UUID id, UUID servicoId) {
        var profissional = profissionalRepositorio.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        profissional.removerServico(servicoId);
        profissionalRepositorio.salvar(profissional);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarAgendamentosProfissional(UUID profissionalId, String status) {
        var profissional = profissionalRepositorio.buscarPorId(profissionalId)
            .orElseThrow(() -> new RegraNegocioExcecao("Profissional não encontrado"));

        var agendamentos = agendamentoRepositorio.buscarPorProfissional(profissionalId);

        if (status != null && !status.isEmpty()) {
            try {
                Agendamento.Status statusEnum = Agendamento.Status.valueOf(status.toUpperCase());
                return agendamentos.stream()
                    .filter(a -> a.getStatus() == statusEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                throw new RegraNegocioExcecao("Status inválido: " + status);
            }
        }

        return agendamentos;
    }
}
