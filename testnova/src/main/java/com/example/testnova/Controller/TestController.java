package com.example.testnova.Controller;

import com.example.testnova.Model.TestResult;
import com.example.testnova.Service.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;
    private final ObjectMapper objectMapper; // Ajoutez cette ligne


    public TestController(TestService testService, ObjectMapper objectMapper) {
        this.testService = testService;
        this.objectMapper = objectMapper; // Initialisez-le ici
    }
    @PostMapping(value = "/generateTest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateTest(@RequestBody Map<String, Object> analysis) {
        try {
            System.out.println("[TestController] Requête reçue pour génération test");

            if (analysis == null || analysis.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Données d'analyse manquantes"));
            }

            String analysisJson = objectMapper.writeValueAsString(analysis);
            Object testResult = testService.generateTest(analysisJson);

            // Vérifier si c'est une erreur
            if (testResult instanceof Map && ((Map<?, ?>) testResult).containsKey("error")) {
                return ResponseEntity.badRequest().body(testResult);
            }

            System.out.println("[TestController] Test généré avec succès");
            return ResponseEntity.ok(testResult);

        } catch (Exception e) {
            System.err.println("[TestController] Erreur génération test: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de la génération du test: " + e.getMessage()));
        }
    }
    // src/main/java/com/example/testnova/Controller/TestController.java
    @PostMapping("/correct")
    public ResponseEntity<TestResult> correctTest(@RequestBody Map<String, Object> submission) {
        try {
            TestResult result = testService.correctTest(submission);

            // Validate score is on /10 scale
            if (result.getTotalScore() > 10) {
                System.out.println("[TestController] Warning: Score > 10, normalizing");
                result.setTotalScore(result.getTotalScore() / 10);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("[TestController] Erreur correction test: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}