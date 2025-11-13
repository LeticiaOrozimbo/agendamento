package com.agendamento.dominio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Endereco {
    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String cep;

    private Endereco(String logradouro, String numero, String complemento,
                    String bairro, String cidade, String estado, String cep) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (cep == null || !cep.matches("\\d{5}-\\d{3}|\\d{8}")) {
            throw new IllegalArgumentException("CEP deve estar no formato 00000-000 ou 00000000");
        }
        // Normaliza o CEP para o formato com hífen se não estiver
        if (cep.matches("\\d{8}")) {
            this.cep = cep.substring(0, 5) + "-" + cep.substring(5);
        } else {
            this.cep = cep;
        }

        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public static Endereco criar(String logradouro, String numero, String complemento,
                               String bairro, String cidade, String estado, String cep) {
        return new Endereco(logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public void setLogradouro(String logradouro) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        this.logradouro = logradouro;
    }

    public void setNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        this.numero = numero;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        this.bairro = bairro;
    }

    public void setCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        this.estado = estado;
    }

    public void setCep(String cep) {
        if (cep == null || !cep.matches("\\d{8}|\\d{5}-\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido");
        }
        this.cep = cep;
    }
}
