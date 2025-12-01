// src/main/java/com/example/testnova/Model/UserTestResult.java
package com.example.testnova.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_test_results")
public class UserTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELATION MODIFIÉE → Candidat au lieu de User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_result_candidat"))
    private Candidat candidat;

    @Column(name = "test_result_json", columnDefinition = "TEXT", nullable = false)
    private String testResultJson;

    @Column(name = "date_taken", nullable = false)
    private LocalDateTime dateTaken = LocalDateTime.now();

    // Constructeurs
    public UserTestResult() {
    }

    public UserTestResult(Candidat candidat, String testResultJson) {
        this.candidat = candidat;
        this.testResultJson = testResultJson;
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public String getTestResultJson() {
        return testResultJson;
    }

    public void setTestResultJson(String testResultJson) {
        this.testResultJson = testResultJson;
    }

    public LocalDateTime getDateTaken() {
        return dateTaken;
    }
}