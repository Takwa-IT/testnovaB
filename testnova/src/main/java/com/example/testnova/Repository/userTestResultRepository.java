// src/main/java/com/example/testnova/Repository/UserTestResultRepository.java
package com.example.testnova.Repository;

import com.example.testnova.Model.UserTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface userTestResultRepository extends JpaRepository<UserTestResult, Long> {

    // IMPORTANT : on utilise "user.id" maintenant
    List<UserTestResult> findByUser_IdOrderByDateTakenDesc(Long userId);

    // Ou encore mieux (plus propre)
    List<UserTestResult> findByUserIdOrderByDateTakenDesc(Long userId);
}