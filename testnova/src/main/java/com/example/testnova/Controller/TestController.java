package com.example.testnova.Controller;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.TestResult;
import com.example.testnova.Model.UserTestResult;
import com.example.testnova.Repository.CandidatRepository;
import com.example.testnova.Repository.userTestResultRepository;
import com.example.testnova.Service.TestService;
import com.example.testnova.Service.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;
    private final ObjectMapper objectMapper;
    private final userTestResultRepository userTestResultRepository;
    private final CandidatRepository candidatRepository;

    public TestController(TestService testService,
            ObjectMapper objectMapper,
            userTestResultRepository userTestResultRepository,
            CandidatRepository candidatRepository) {
        this.testService = testService;
        this.objectMapper = objectMapper;
        this.userTestResultRepository = userTestResultRepository;
        this.candidatRepository = candidatRepository;
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

            if (testResult instanceof Map && ((Map<?, ?>) testResult).containsKey("error")) {
                return ResponseEntity.badRequest().body(testResult);
            }

            System.out.println("[TestController] Test généré avec succès");
            return ResponseEntity.ok(testResult);

        } catch (Exception e) {
            System.err.println("[TestController] Erreur génération test: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de la génération du test: " + e.getMessage()));
        }
    }

    // ENDPOINT CORRIGÉ : utilise l'utilisateur connecté automatiquement
    @PostMapping("/correct")
    public ResponseEntity<TestResult> correctTest(
            @RequestBody Map<String, Object> submission,
            Authentication authentication) { // LIGNE OBLIGATOIRE

        try {
            TestResult result = testService.correctTest(submission);

            // Normalisation du score
            if (result.getTotalScore() > 10.0) {
                result.setTotalScore(result.getTotalScore() / 10.0);
            }

            // SAUVEGARDE AVEC RELATION RÉELLE
            if (authentication != null && authentication.isAuthenticated()) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Candidat candidat = candidatRepository.findById(userDetails.getId())
                        .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

                String jsonResult = objectMapper.writeValueAsString(result);
                UserTestResult testResult = new UserTestResult(candidat, jsonResult);
                userTestResultRepository.save(testResult);

                System.out.println("TEST SAUVEGARDÉ EN BASE POUR CANDIDAT ID = " + candidat.getId());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<TestResult>> getUserHistory(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(401).build();
        }

        Long candidatId = userDetails.getId();
        List<UserTestResult> saved = userTestResultRepository.findByCandidatIdOrderByDateTakenDesc(candidatId);

        List<TestResult> results = saved.stream()
                .map(utr -> {
                    try {
                        return objectMapper.readValue(utr.getTestResultJson(), TestResult.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }
}