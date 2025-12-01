package com.example.testnova.Dto;

import com.example.testnova.Model.CandidateStatus;

public class DecisionRequest {
    private Long candidateId;
    private CandidateStatus decision;  // ACCEPTED ou REJECTED
    private String comment;

    // Constructeurs
    public DecisionRequest() {}

    public DecisionRequest(Long candidateId, CandidateStatus decision, String comment) {
        this.candidateId = candidateId;
        this.decision = decision;
        this.comment = comment;
    }

    // Getters/Setters
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public CandidateStatus getDecision() { return decision; }
    public void setDecision(CandidateStatus decision) { this.decision = decision; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
