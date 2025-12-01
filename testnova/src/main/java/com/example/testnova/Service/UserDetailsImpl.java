// src/main/java/com/example/testnova/Service/UserDetailsImpl.java
package com.example.testnova.Service;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.Compte;
import com.example.testnova.Model.HR;
import com.example.testnova.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Compte compte;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Compte compte, Collection<? extends GrantedAuthority> authorities) {
        this.compte = compte;
        this.authorities = authorities;
    }

    /**
     * Construction avec héritage POO : le rôle est déterminé par le TYPE de classe
     */
    public static UserDetailsImpl build(Compte compte) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Déterminer le rôle basé sur le type de classe (héritage POO)
        if (compte instanceof HR) {
            authorities.add(new SimpleGrantedAuthority("ROLE_HR"));
        } else if (compte instanceof Candidat) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CANDIDAT"));
        } else if (compte instanceof User) {
            // Rétrocompatibilité : utiliser les rôles de la table roles
            User user = (User) compte;
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());
        }

        return new UserDetailsImpl(compte, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return compte.getId();
    }

    public String getNom() {
        return compte.getNom();
    }

    public String getPrenom() {
        return compte.getPrenom();
    }

    public String getEmail() {
        return compte.getEmail();
    }

    public String getTelephone() {
        if (compte instanceof Candidat) {
            return ((Candidat) compte).getTelephone();
        }
        return null;
    }

    public String getVille() {
        if (compte instanceof Candidat) {
            return ((Candidat) compte).getVille();
        }
        return null;
    }

    public String getPosteRecherche() {
        if (compte instanceof Candidat) {
            return ((Candidat) compte).getPosteRecherche();
        }
        return null;
    }

    public Compte getCompte() {
        return compte;
    }

    @Override
    public String getPassword() {
        return compte.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return compte.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl userDetails = (UserDetailsImpl) o;
        return Objects.equals(compte.getId(), userDetails.compte.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(compte.getId());
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + compte.getId() +
                ", email='" + compte.getEmail() + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}