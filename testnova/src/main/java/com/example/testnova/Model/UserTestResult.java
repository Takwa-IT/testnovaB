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

    // VRAIE RELATION → crée la FK automatiquement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_result_user"))
    private User user;

    @Column(name = "test_result_json", columnDefinition = "TEXT", nullable = false)
    private String testResultJson;

    @Column(name = "date_taken", nullable = false)
    private LocalDateTime dateTaken = LocalDateTime.now();

    // Constructeurs
    public UserTestResult() {
    }

    public UserTestResult(User user, String testResultJson) {
        this.user = user;
        this.testResultJson = testResultJson;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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