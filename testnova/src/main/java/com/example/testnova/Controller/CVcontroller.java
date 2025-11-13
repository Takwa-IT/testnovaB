package com.example.testnova.Controller;

import com.example.testnova.Model.Cvanalyse;
import com.example.testnova.Service.CVservice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

public class CVcontroller {

    private final CVservice cvservice;

    public CVcontroller(CVservice cvservice) {
        this.cvservice = cvservice;
    }

    @PostMapping(value = "/analysecv", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> body) {
        try {
            System.out.println("[Backend] Requête reçue pour analyse CV");
            String textcv = body.get("textcv");

            if (textcv == null || textcv.trim().isEmpty()) {
                System.out.println("[Backend] Erreur: texte CV vide");
                return ResponseEntity.badRequest().body(Map.of("error", "Le texte du CV est vide"));
            }

            System.out.println("[Backend] Texte CV reçu: " + textcv.substring(0, Math.min(100, textcv.length())) + "...");

            Object result = cvservice.analysecv(textcv);

            System.out.println("[Backend] Analyse terminée avec succès");
            // Retourner 201 Created avec le JSON résultant (inclut l'id en base)
            return ResponseEntity.status(201).body(result);

        } catch (Exception e) {
            System.err.println("[Backend] Erreur lors de l'analyse: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("cvparuser/{id}")
    public ResponseEntity<List<Cvanalyse>> getAllCVanalyseParUser(@PathVariable Long id) {
        List<Cvanalyse> analyses = cvservice.findAllByUserId(id);
        return ResponseEntity.ok(analyses);
    }
    @PostMapping("/analyse-offre")
    public Object analyserCvAvecOffre(@RequestBody Map<String, Object> payload) {
        try {
            String cvText = (String) payload.get("cvText");
            Object offre = payload.get("offre");

            if (cvText == null || offre == null) {
                return Map.of("error", "Le champ 'cvText' et 'offre' sont obligatoires.");
            }

            Object result = cvservice.analysecvoffre(cvText, offre);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Erreur lors de l'analyse du CV : " + e.getMessage());
        }
    }


}