package com.example.testnova.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ProblemResult {
    private String userSolution;
    private String explanation;
    private double score;

    @JsonProperty("isGood")
    private boolean isGood;

    public ProblemResult() {}

    public String getUserSolution() {
        return userSolution;
    }

    public void setUserSolution(String userSolution) {
        this.userSolution = userSolution;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }
}
