package com.agendamento.dominio.repositorios;

import com.agendamento.dominio.entidades.Cliente;
import java.util.List;

public interface ClienteRepositorio extends RepositorioBase<Cliente> {
    List<Cliente> buscarPorNome(String nome);
    List<Cliente> buscarPorEmail(String email);
    boolean emailJaCadastrado(String email);
}
