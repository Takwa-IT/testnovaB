// src/main/java/com/example/testnova/Service/TestService.java
package com.example.testnova.Service;

import com.example.testnova.Model.TestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class TestService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public TestService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    // ========================================================================
    // 1. GÉNÉRATION DU TEST
    // ========================================================================
    public Object generateTest(String cvAnalysisJson) {
        System.out.println("[TestService] Début génération test personnalisé");

        String prompt = """
                Tu es un expert technique. Génère un test personnalisé basé UNIQUEMENT sur les compétences techniques listées dans la section "skills" de cette analyse CV (JSON). Ignore le résumé et les expériences et le niveau pour les questions.
                %s
                        
                        CRITÈRES IMPORTANTS :
                                    - 10 questions techniques maximum
                                    - Mélange de types : multiple (4 options) et texte court
                                    - Questions adaptées au niveau détecté
                                    - Un problème pratique réaliste
                                    - Réponses correctes incluses pour la correction
                                   
                        - TOUS les textes doivent être sur une seule ligne (pas de retours à la ligne dans les chaînes JSON)
                                                                                   
                                                                                    FORMAT JSON STRICT - IMPORTANT : Pas de retours à la ligne dans les valeurs de texte :                            {
                                      "questions": [
                                        {
                                          "id": 1,
                                          "text": "Question claire et concise",
                                          "type": "multiple", // ou "text"
                                          "options": ["A", "B", "C", "D"], // seulement si type="multiple"
                                          "correctAnswer": "B" // ou texte pour type="text"
                                        }
                                      ],
                 "problem": {
                                     "description": "Description détaillée du problème pratique",
                                     "expectedSolution": "Solution attendue ou critères d'évaluation"
                                   }
                                 }
                                
                                 Réponds UNIQUEMENT avec le JSON valide, sans commentaires.
                """.formatted(cvAnalysisJson);

        try {
            var response = chatClient.prompt()
                    .user(prompt)
                    .call();

            String jsonString = Objects.requireNonNull(response.content());
            System.out.println("[TestService] Réponse brute complète: " + jsonString);

            String cleanJson = extractJsonFromResponse(jsonString);
            System.out.println("[TestService] JSON nettoyé: " + cleanJson);

            // Validation supplémentaire du JSON
            Object parsedJson = objectMapper.readValue(cleanJson, Object.class);
            System.out.println("[TestService] JSON parsé avec succès");
            return parsedJson;

        } catch (Exception e) {
            System.err.println("[TestService] Erreur détaillée: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du test: " + e.getMessage(), e);
        }
    }

    // Méthode de nettoyage (copiée de CVservice pour indépendance)
    private String extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "{}";
        }

        String cleaned = response.trim();

        // Supprimer les blocs de code
        cleaned = cleaned.replaceAll("```json", "").replaceAll("```", "");

        // Trouver le premier { et dernier }
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}') + 1;

        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end).trim();
        }

        // Nettoyer les commentaires
        cleaned = cleaned.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "").trim();

        // Validation basique - ajouter des virgules manquantes dans les tableaux
        cleaned = cleaned.replaceAll("(?m)(\"\\s*\\])\\s*\"", "$1,\"");

        System.out.println("[TestService] JSON après nettoyage: " + cleaned);
        return cleaned.isEmpty() ? "{}" : cleaned;
    }

    // ========================================================================
    // 2. CORRECTION DU TEST PAR IA
    // ========================================================================
    // ========================================================================
// 2. CORRECTION DU TEST - VERSION SIMPLIFIÉE
// ========================================================================
    public TestResult correctTest(Map<String, Object> submission) throws JsonProcessingException {
        System.out.println("[TestService] Soumission reçue pour correction");

        String submissionJson = objectMapper.writeValueAsString(submission);

        String prompt = """
                Évalue les réponses du candidat et fournis une analyse détaillée.
                Pour chaque question, indique si la réponse est correcte (isCorrect: true/false).
                    
                IMPORTANT : Ne calcule PAS les scores totaux, je m'en chargerai.
                Réponds UNIQUEMENT avec ce format JSON :

                {
                  "questionResults": [
                    {
                      "questionId": 1,
                      "questionText": "Question",
                      "userAnswer": "Réponse utilisateur",
                      "correctAnswer": "Réponse correcte",
                      "isCorrect": false,
                      "explanation": "Explication"
                    }
                  ],
                  "feedback": "Feedback général",
                  "problemResult": {
                    "userSolution": "Solution proposée",
                    "explanation": "Évaluation problème",
                    "score": 5.0,
                    "isGood": false
                  }
                }

                DONNÉES : %s
                """.formatted(submissionJson);

        try {
            var response = chatClient.prompt().user(prompt).call();
            String jsonString = response.content();
            String cleanJson = extractJsonFromResponse(jsonString);

            // Parser le résultat partiel
            TestResult partialResult = objectMapper.readValue(cleanJson, TestResult.class);

            // CALCUL MANUEL DES SCORES
            return calculateScoresManually(partialResult);

        } catch (Exception e) {
            System.err.println("[TestService] Erreur correction: " + e.getMessage());
            throw new RuntimeException("Erreur correction test", e);
        }
    }

    /**
     * Calcule les scores manuellement pour garantir l'exactitude
     */
    private TestResult calculateScoresManually(TestResult partialResult) {
        if (partialResult.getQuestionResults() == null) {
            throw new RuntimeException("Aucun résultat de question");
        }

        // Compter les réponses correctes
        long correctAnswers = partialResult.getQuestionResults().stream()
                .filter(q -> q.isCorrect())
                .count();

        int totalQuestions = partialResult.getQuestionResults().size();

        // Calculer les scores
        double totalScore = totalQuestions > 0 ? (correctAnswers / (double) totalQuestions) * 10.0 : 0;
        double scorePercentage = totalQuestions > 0 ? (correctAnswers / (double) totalQuestions) * 100.0 : 0;

        // Arrondir à 1 décimale
        totalScore = Math.round(totalScore * 10.0) / 10.0;
        scorePercentage = Math.round(scorePercentage * 10.0) / 10.0;

        // Mettre à jour le résultat
        partialResult.setTotalScore(totalScore);
        partialResult.setScorePercentage(scorePercentage);
        partialResult.setCorrectAnswers((int) correctAnswers);
        partialResult.setTotalQuestions(totalQuestions);
        partialResult.setProblemMaxScore(10); // Score max pour le problème

        System.out.println("[TestService] Score calculé: " + correctAnswers + "/" + totalQuestions +
                " = " + totalScore + "/10 (" + scorePercentage + "%)");

        return partialResult;
    }
}