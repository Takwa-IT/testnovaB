package com.example.testnova.Model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class GeneratedTest {
    private List<Question> questions;
    private Problem problem;

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
}
