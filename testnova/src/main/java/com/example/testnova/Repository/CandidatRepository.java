package com.example.testnova.Repository;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    // Trouver un candidat par email
    Optional<Candidat> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Trouver les candidats par statut
    List<Candidat> findByStatus(CandidateStatus status);

    // Compter les candidats par statut
    long countByStatus(CandidateStatus status);

    // Recherche par nom ou prénom (insensible à la casse)
    List<Candidat> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Recherche par ville
    List<Candidat> findByVille(String ville);

    // Recherche par poste recherché
    List<Candidat> findByPosteRecherche(String posteRecherche);
}
