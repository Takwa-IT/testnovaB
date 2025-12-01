package com.example.testnova.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cvanalyse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String resume; // "Résumé intelligent du profil"

    private LocalDate dateAnalyse = LocalDate.now();

    // Relation avec le candidat qui possède ce CV
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"motDePasse", "cvanalyses", "password"})
    private Candidat user;

    // Liste des compétences techniques
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cv_analyse_id")
    private List<Skill> Skills;

    // Liste des expériences professionnelles
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cv_analyse_id")
    private List<Experience> experiences;

    // Explicit setters/getters to avoid Lombok compile-time issues in some environments
    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getResume() {
        return this.resume;
    }

    public List<Skill> getSkills() {
        return this.Skills;
    }

    public void setSkills(List<Skill> skills) {
        this.Skills = skills;
    }

    public List<Experience> getExperiences() {
        return this.experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidat getUser() {
        return this.user;
    }

    public void setUser(Candidat user) {
        this.user = user;
    }


}
