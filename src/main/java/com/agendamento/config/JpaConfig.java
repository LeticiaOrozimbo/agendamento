package com.agendamento.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
    "com.agendamento.infraestrutura.repositorio",
    "com.agendamento.aplicacao",
    "com.agendamento.api"
})
@EntityScan("com.agendamento.dominio.entidades")
@EnableJpaRepositories("com.agendamento.infraestrutura.repositorio")
public class JpaConfig {
}
