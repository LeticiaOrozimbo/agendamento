package com.agendamento.dominio.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioBase<T> {
    T salvar(T entidade);
    void excluir(UUID id);
    Optional<T> buscarPorId(UUID id);
    List<T> buscarTodos();
    boolean existe(UUID id);
}
