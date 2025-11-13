package com.agendamento.dominio.excecao;

public class RegraNegocioExcecao extends RuntimeException {
    public RegraNegocioExcecao(String mensagem) {
        super(mensagem);
    }

    public RegraNegocioExcecao(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
