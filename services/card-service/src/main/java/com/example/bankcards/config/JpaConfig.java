package com.example.bankcards.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.example.shared.entity") // модуль shared в gradle
@EnableJpaRepositories("com.example.shared.repository") // модуль shared в gradle
public class JpaConfig {
}
