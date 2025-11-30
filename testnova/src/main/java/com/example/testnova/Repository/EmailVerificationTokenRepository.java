package com.example.testnova.Repository;

import com.example.testnova.Model.EmailVerificationToken;
import com.example.testnova.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUserAndConfirmedFalse(User user);
    void deleteByUser(User user);
}
