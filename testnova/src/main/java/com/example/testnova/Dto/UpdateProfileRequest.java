// src/main/java/com/example/testnova/Dto/UpdateProfileRequest.java
package com.example.testnova.Dto;

public class UpdateProfileRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ville;
    private String posteRecherche;

    // Getters et Setters
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
    
    public String getPosteRecherche() { return posteRecherche; }
    public void setPosteRecherche(String posteRecherche) { this.posteRecherche = posteRecherche; }
}
