package com.example.testnova.Controller;

import com.example.testnova.Dto.CandidateDTO;
import com.example.testnova.Dto.DecisionRequest;
import com.example.testnova.Dto.HRDashboardStatsDTO;
import com.example.testnova.Service.HRService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hr")
@PreAuthorize("hasRole('HR')")
public class HRController {

    private final HRService hrService;

    public HRController(HRService hrService) {
        this.hrService = hrService;
    }

    /**
     * GET /api/hr/candidates
     * Liste tous les candidats avec leur dernier score de test
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        try {
            List<CandidateDTO> candidates = hrService.getAllCandidates();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/hr/candidates/{id}
     * Détails d'un candidat spécifique
     */
    @GetMapping("/candidates/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
        try {
            Optional<CandidateDTO> candidate = hrService.getCandidateById(id);
            
            if (candidate.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(candidate.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Erreur lors de la récupération du candidat"));
        }
    }

    /**
     * POST /api/hr/candidates/decision
     * Accepter ou rejeter un candidat
     * Body: { "candidateId": 1, "decision": "ACCEPTED", "comment": "Bon profil" }
     */
    @PostMapping("/candidates/decision")
    public ResponseEntity<?> updateCandidateDecision(@RequestBody DecisionRequest request) {
        try {
            hrService.updateCandidateStatus(request.getCandidateId(), request.getDecision());
            
            return ResponseEntity.ok(Map.of(
                "message", "Décision enregistrée avec succès",
                "candidateId", request.getCandidateId(),
                "decision", request.getDecision().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Erreur lors de l'enregistrement de la décision"));
        }
    }

    /**
     * GET /api/hr/dashboard/stats
     * Statistiques du dashboard : total, pending, accepted, rejected
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<HRDashboardStatsDTO> getDashboardStats() {
        try {
            HRDashboardStatsDTO stats = hrService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
