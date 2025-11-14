package com.example.testnova.Model;

import java.util.List;

public class TestResult {
    private double totalScore;  // Now 0-10 instead of 0-100
    private double scorePercentage;  // Added: percentage for display (0-100)
    private int correctAnswers;
    private int totalQuestions;
    private List<QuestionResult> questionResults;
    private String feedback;
    private ProblemResult problemResult;
    private int problemMaxScore;

    // Getters and Setters
    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public ProblemResult getProblemResult() {
        return problemResult;
    }

    public void setProblemResult(ProblemResult problemResult) {
        this.problemResult = problemResult;
    }

    public int getProblemMaxScore() {
        return problemMaxScore;
    }

    public void setProblemMaxScore(int problemMaxScore) {
        this.problemMaxScore = problemMaxScore;
    }
}