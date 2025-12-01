// src/main/java/com/example/testnova/Service/UserDetailsImpl.java
package com.example.testnova.Service;

import com.example.testnova.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private User user;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(user, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return user.getId();
    }

    public String getNom() {
        return user.getNom();
    }

    public String getPrenom() {
        return user.getPrenom();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getTelephone() {
        return user.getTelephone();
    }

    public String getVille() {
        return user.getVille();
    }

    public String getPosteRecherche() {
        return user.getPosteRecherche();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return Objects.equals(user.getId(), userDetails.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId());
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + user.getId() +
                ", email='" + user.getEmail() + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}