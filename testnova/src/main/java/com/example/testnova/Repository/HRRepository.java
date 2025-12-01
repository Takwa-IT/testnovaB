package com.example.testnova.Repository;

import com.example.testnova.Model.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HRRepository extends JpaRepository<HR, Long> {

    // Trouver un HR par email
    Optional<HR> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Recherche par entreprise
    Optional<HR> findByEntreprise(String entreprise);
}
