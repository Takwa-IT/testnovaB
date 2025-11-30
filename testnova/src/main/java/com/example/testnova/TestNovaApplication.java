package com.example.testnova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestNovaApplication {
    public static void main(String[] args) {
        System.out.println("=== Démarrage de TestNova Backend ===");
        SpringApplication.run(TestNovaApplication.class, args);
        System.out.println("=== Backend prêt sur http://localhost:8081 ===");
    }
}
