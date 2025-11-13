package com.agendamento.dominio.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor
public class Cliente {
    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Embedded
    private Endereco endereco;

    @Column(name = "calendario_integrado")
    private boolean calendarioIntegrado;

    @Column(name = "email_calendario")
    private String emailCalendario;

    private Cliente(UUID id, String nome, String email, String telefone, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (telefone == null || !telefone.matches("^\\+?[0-9]{10,13}$")) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.calendarioIntegrado = false;
        this.emailCalendario = null;
    }

    public static Cliente criar(String nome, String email, String telefone, Endereco endereco) {
        return new Cliente(UUID.randomUUID(), nome, email, telefone, endereco);
    }

    public void integrarCalendario(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido para integração com calendário");
        }
        this.calendarioIntegrado = true;
        this.emailCalendario = email;
    }

    public void removerIntegracaoCalendario() {
        this.calendarioIntegrado = false;
        this.emailCalendario = null;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.nome = nome;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || !telefone.matches("^\\+?[0-9]{10,13}$")) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        this.telefone = telefone;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        this.endereco = endereco;
    }

    public String getEmailCalendario() {
        return this.emailCalendario;
    }

    public String getEmail() {
        return this.emailCalendario != null ? this.emailCalendario : this.email;
    }

    public boolean getCalendarioIntegrado() {
        return this.calendarioIntegrado;
    }
    }

