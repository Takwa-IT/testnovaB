package com.example.testnova.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "hr")
public class HR extends Compte {

    @Column
    private String entreprise;

    @Column
    private String departement;

    @Column
    private LocalDate dateInscription = LocalDate.now();

    @Column
    private Boolean emailVerified = false;

    // Constructeurs
    public HR() {
        super();
    }

    public HR(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters/Setters
    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
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
}
