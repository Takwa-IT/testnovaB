// src/main/java/com/example/testnova/Repository/UserRepository.java
package com.example.testnova.Repository;

import com.example.testnova.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Trouver les utilisateurs par rôle
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // Trouver les utilisateurs avec un rôle spécifique (enum)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = com.example.testnova.Model.ERole.ROLE_CANDIDAT")
    List<User> findAllCandidats();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = com.example.testnova.Model.ERole.ROLE_HR")
    List<User> findAllHR();

    // Recherche d'utilisateurs par nom ou prénom (insensible à la casse)
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Compter le nombre d'utilisateurs par rôle
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    // Vérifier si un utilisateur a un rôle spécifique
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE u.email = :email AND r.name = :roleName")
    boolean hasRole(@Param("email") String email, @Param("roleName") String roleName);

    // Trouver les utilisateurs avec pagination et tri
    // (Utiliser Pageable directement avec les méthodes de JpaRepository)

    // Mettre à jour le mot de passe
    @Modifying
    @Query("UPDATE User u SET u.motDePasse = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
}