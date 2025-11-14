package com.example.testnova.Model;

public class Problem {
    private String description;
    private String expectedSolution; // Pour la correction

    // Getters et setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpectedSolution() { return expectedSolution; }
    public void setExpectedSolution(String expectedSolution) { this.expectedSolution = expectedSolution; }
}
