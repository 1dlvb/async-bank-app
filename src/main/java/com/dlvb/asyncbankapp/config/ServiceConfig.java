package com.dlvb.asyncbankapp.config;

import com.dlvb.asyncbankapp.auditor.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Конфигурация для бинов сервиса
 * @author Matushkin Anton
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ServiceConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

}
