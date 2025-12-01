package com.example.testnova.Dto;

public class HRDashboardStatsDTO {
    private long totalCandidates;
    private long pendingCandidates;
    private long acceptedCandidates;
    private long rejectedCandidates;

    // Constructeurs
    public HRDashboardStatsDTO() {}

    public HRDashboardStatsDTO(long totalCandidates, long pendingCandidates, 
                               long acceptedCandidates, long rejectedCandidates) {
        this.totalCandidates = totalCandidates;
        this.pendingCandidates = pendingCandidates;
        this.acceptedCandidates = acceptedCandidates;
        this.rejectedCandidates = rejectedCandidates;
    }

    // Getters/Setters
    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }

    public long getPendingCandidates() { return pendingCandidates; }
    public void setPendingCandidates(long pendingCandidates) { this.pendingCandidates = pendingCandidates; }

    public long getAcceptedCandidates() { return acceptedCandidates; }
    public void setAcceptedCandidates(long acceptedCandidates) { this.acceptedCandidates = acceptedCandidates; }

    public long getRejectedCandidates() { return rejectedCandidates; }
    public void setRejectedCandidates(long rejectedCandidates) { this.rejectedCandidates = rejectedCandidates; }
}
