package com.example.testnova.Controller;

import com.example.testnova.Service.CVservice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CVcontroller {

    private final CVservice cvservice;

    public CVcontroller(CVservice cvservice) {
        this.cvservice = cvservice;
    }

    @PostMapping("/api/analyse-offre")
    public Object analyserCvAvecOffre(@RequestBody Map<String, Object> payload) {
        try {
            String cvText = (String) payload.get("cvText");
            Object offre = payload.get("offre");
            Long userId = null;
            Object userIdObj = payload.get("userId");
            if (userIdObj != null) {
                userId = Long.valueOf(userIdObj.toString());
            }

            if (cvText == null || offre == null) {
                return Map.of("error", "Le champ 'cvText' et 'offre' sont obligatoires.");
            }

            Object result = cvservice.analysecvoffre(cvText, offre, userId);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Erreur lors de l'analyse du CV : " + e.getMessage());
        }
    }

    // Récupérer tous les CV analysés d'un utilisateur
    @GetMapping("/cvparuser/{userId}")
    public ResponseEntity<?> getCvsByUser(@PathVariable Long userId) {
        try {
            System.out.println("[Backend] Requête reçue pour récupérer les CV de l'utilisateur: " + userId);
            var cvs = cvservice.getCvsByUserId(userId);
            
            // DEBUG: Afficher les détails de chaque CV
            System.out.println("[Backend] Nombre de CV trouvés: " + (cvs != null ? cvs.size() : 0));
            if (cvs != null) {
                for (var cv : cvs) {
                    System.out.println("[Backend] CV ID: " + cv.getId());
                    System.out.println("[Backend]   - Resume: " + (cv.getResume() != null ? cv.getResume().substring(0, Math.min(50, cv.getResume().length())) + "..." : "null"));
                    System.out.println("[Backend]   - Skills: " + (cv.getSkills() != null ? cv.getSkills().size() : "null"));
                    System.out.println("[Backend]   - Experiences: " + (cv.getExperiences() != null ? cv.getExperiences().size() : "null"));
                    if (cv.getSkills() != null) {
                        for (var skill : cv.getSkills()) {
                            System.out.println("[Backend]     Skill: " + skill.getName() + " (" + skill.getLevel() + ")");
                        }
                    }
                    if (cv.getExperiences() != null) {
                        for (var exp : cv.getExperiences()) {
                            System.out.println("[Backend]     Exp: " + exp.getRole() + " @ " + exp.getCompany());
                        }
                    }
                }
            }
            
            return ResponseEntity.ok(cvs);
        } catch (Exception e) {
            System.err.println("[Backend] Erreur lors de la récupération des CV: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer un CV par son ID
    @GetMapping("/cv/{id}")
    public ResponseEntity<?> getCvById(@PathVariable Long id) {
        try {
            System.out.println("[Backend] Requête reçue pour récupérer le CV: " + id);
            var cv = cvservice.getCvById(id);
            if (cv == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(cv);
        } catch (Exception e) {
            System.err.println("[Backend] Erreur lors de la récupération du CV: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}