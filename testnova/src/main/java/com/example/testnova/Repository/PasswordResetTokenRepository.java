package com.example.testnova.Repository;

import com.example.testnova.Model.Compte;
import com.example.testnova.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByCompteAndUsedFalse(Compte compte);
    void deleteByCompte(Compte compte);
}
