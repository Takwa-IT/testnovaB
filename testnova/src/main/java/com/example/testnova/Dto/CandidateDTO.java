package com.example.testnova.Dto;

import com.example.testnova.Model.CandidateStatus;
import java.time.LocalDate;

public class CandidateDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ville;
    private Integer score;  // Score du dernier test (peut être null)
    private String poste;
    private LocalDate dateApplication;
    private CandidateStatus status;

    // Constructeurs
    public CandidateDTO() {}

    public CandidateDTO(Long id, String nom, String prenom, String email, String telephone, 
                        String ville, Integer score, String poste, LocalDate dateApplication, 
                        CandidateStatus status) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.ville = ville;
        this.score = score;
        this.poste = poste;
        this.dateApplication = dateApplication;
        this.status = status;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public LocalDate getDateApplication() { return dateApplication; }
    public void setDateApplication(LocalDate dateApplication) { this.dateApplication = dateApplication; }

    public CandidateStatus getStatus() { return status; }
    public void setStatus(CandidateStatus status) { this.status = status; }
}
