package com.example.testnova.Service;

import com.example.testnova.Dto.CandidateDTO;
import com.example.testnova.Dto.HRDashboardStatsDTO;
import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.CandidateStatus;
import com.example.testnova.Model.UserTestResult;
import com.example.testnova.Repository.CandidatRepository;
import com.example.testnova.Repository.userTestResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HRService {

    private final CandidatRepository candidatRepository;
    private final userTestResultRepository testResultRepository;
    private final ObjectMapper objectMapper;

    public HRService(CandidatRepository candidatRepository, 
                     userTestResultRepository testResultRepository,
                     ObjectMapper objectMapper) {
        this.candidatRepository = candidatRepository;
        this.testResultRepository = testResultRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Récupère tous les candidats avec leur score du dernier test
     */
    public List<CandidateDTO> getAllCandidates() {
        List<Candidat> candidats = candidatRepository.findAll();
        List<CandidateDTO> candidateDTOs = new ArrayList<>();

        for (Candidat candidat : candidats) {
            Integer score = getLatestTestScore(candidat.getId());
            
            CandidateDTO dto = new CandidateDTO(
                candidat.getId(),
                candidat.getNom(),
                candidat.getPrenom(),
                candidat.getEmail(),
                candidat.getTelephone(),
                candidat.getVille(),
                score,
                candidat.getPosteRecherche(),
                candidat.getDateInscription(),
                candidat.getStatus()
            );
            candidateDTOs.add(dto);
        }

        return candidateDTOs;
    }

    /**
     * Récupère un candidat par ID avec ses détails
     */
    public Optional<CandidateDTO> getCandidateById(Long id) {
        Optional<Candidat> candidatOpt = candidatRepository.findById(id);
        
        if (candidatOpt.isEmpty()) {
            return Optional.empty();
        }

        Candidat candidat = candidatOpt.get();
        Integer score = getLatestTestScore(candidat.getId());

        CandidateDTO dto = new CandidateDTO(
            candidat.getId(),
            candidat.getNom(),
            candidat.getPrenom(),
            candidat.getEmail(),
            candidat.getTelephone(),
            candidat.getVille(),
            score,
            candidat.getPosteRecherche(),
            candidat.getDateInscription(),
            candidat.getStatus()
        );

        return Optional.of(dto);
    }

    /**
     * Met à jour le statut d'un candidat
     */
    @Transactional
    public void updateCandidateStatus(Long candidateId, CandidateStatus status) {
        Candidat candidat = candidatRepository.findById(candidateId)
            .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'ID : " + candidateId));
        
        candidat.setStatus(status);
        candidatRepository.save(candidat);
    }

    /**
     * Récupère les statistiques du dashboard HR
     */
    public HRDashboardStatsDTO getDashboardStats() {
        long total = candidatRepository.count();
        long pending = candidatRepository.countByStatus(CandidateStatus.PENDING);
        long accepted = candidatRepository.countByStatus(CandidateStatus.ACCEPTED);
        long rejected = candidatRepository.countByStatus(CandidateStatus.REJECTED);

        return new HRDashboardStatsDTO(total, pending, accepted, rejected);
    }

    /**
     * Méthode helper pour extraire le score du dernier test
     */
    private Integer getLatestTestScore(Long candidatId) {
        try {
            List<UserTestResult> results = testResultRepository.findByCandidatIdOrderByDateTakenDesc(candidatId);
            
            if (results.isEmpty()) {
                return null;
            }

            UserTestResult latestResult = results.get(0);
            String jsonResult = latestResult.getTestResultJson();

            // Parse le JSON pour extraire le score
            JsonNode rootNode = objectMapper.readTree(jsonResult);
            if (rootNode.has("totalScore")) {
                return rootNode.get("totalScore").asInt();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du score : " + e.getMessage());
            return null;
        }
    }
}
