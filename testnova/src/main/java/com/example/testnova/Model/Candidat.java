package com.example.testnova.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidat")
public class Candidat extends Compte {

    @Column
    private String telephone;

    @Column
    private String ville;

    @Column
    private String posteRecherche;

    @Column
    private LocalDate dateInscription = LocalDate.now();

    @Column
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status = CandidateStatus.PENDING;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cvanalyse> mescvsAnalyses = new ArrayList<>();

    // Constructeurs
    public Candidat() {
        super();
    }

    public Candidat(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters/Setters
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPosteRecherche() {
        return posteRecherche;
    }

    public void setPosteRecherche(String posteRecherche) {
        this.posteRecherche = posteRecherche;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public List<Cvanalyse> getMescvsAnalyses() {
        return mescvsAnalyses;
    }

    public void setMescvsAnalyses(List<Cvanalyse> mescvsAnalyses) {
        this.mescvsAnalyses = mescvsAnalyses;
    }
}
