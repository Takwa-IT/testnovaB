package com.example.testnova.Repository;

import com.example.testnova.Model.Compte;
import com.example.testnova.Model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByCompteAndConfirmedFalse(Compte compte);
    void deleteByCompte(Compte compte);
}
