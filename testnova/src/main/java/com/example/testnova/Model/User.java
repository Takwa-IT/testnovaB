// src/main/java/com/example/testnova/Model/User.java
package com.example.testnova.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user") // ✅ Table nommée "user" pas "users"
public class User extends Compte {

    private LocalDate dateInscription = LocalDate.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cvanalyse> mescvsAnalyses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // 🔥 CONSTRUCTEURS
    public User() {
        super();
    }

    public User(String nom, String prenom, String email, String motDePasse, Set<Role> roles) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roles = roles;
    }

    // 🔥 CORRECTION : Supprimer les méthodes intermédiaires et utiliser directement les getters/setters hérités
    public Long getUserId() {
        return this.id;
    }

    // Getters/Setters pour les champs spécifiques à User
    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public List<Cvanalyse> getMescvsAnalyses() {
        return mescvsAnalyses;
    }

    public void setMescvsAnalyses(List<Cvanalyse> mescvsAnalyses) {
        this.mescvsAnalyses = mescvsAnalyses;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}