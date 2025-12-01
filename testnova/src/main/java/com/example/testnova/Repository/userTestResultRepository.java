// src/main/java/com/example/testnova/Repository/UserTestResultRepository.java
package com.example.testnova.Repository;

import com.example.testnova.Model.UserTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface userTestResultRepository extends JpaRepository<UserTestResult, Long> {

    // Méthodes adaptées pour Candidat au lieu de User
    List<UserTestResult> findByCandidat_IdOrderByDateTakenDesc(Long candidatId);

    // Version alternative (plus propre)
    List<UserTestResult> findByCandidatIdOrderByDateTakenDesc(Long candidatId);
}