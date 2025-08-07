package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableScheduling
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.example.bankcards", "com.example.shared"})
public class CardService {
    public static void main(String[] args) {
        SpringApplication.run(CardService.class, args);
    }
}
