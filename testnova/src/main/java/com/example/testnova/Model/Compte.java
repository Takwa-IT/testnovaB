// src/main/java/com/example/testnova/Model/Compte.java
package com.example.testnova.Model;

import jakarta.persistence.*;

@MappedSuperclass
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String nom;

    protected String prenom;

    @Column(unique = true, nullable = false)
    protected String email;

    protected String motDePasse;

    // 🔥 AJOUT des getters/setters publics pour un accès direct
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Compte() {
    }

    public Compte(Long id, String nom, String prenom, String email, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }
}