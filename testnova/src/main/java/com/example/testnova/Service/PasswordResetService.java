package com.example.testnova.Service;

import com.example.testnova.Model.EmailVerificationToken;
import com.example.testnova.Model.PasswordResetToken;
import com.example.testnova.Model.User;
import com.example.testnova.Repository.EmailVerificationTokenRepository;
import com.example.testnova.Repository.PasswordResetTokenRepository;
import com.example.testnova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                 PasswordResetTokenRepository passwordResetTokenRepository,
                                 EmailVerificationTokenRepository emailVerificationTokenRepository,
                                 EmailService emailService,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== FORGOT PASSWORD ====================

    @Transactional
    public void sendPasswordResetEmail(String email) {
        System.out.println("📧 Demande de réinitialisation pour: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        // Générer un token unique
        String token = UUID.randomUUID().toString();

        // Supprimer les anciens tokens non utilisés
        passwordResetTokenRepository.findByUserAndUsedFalse(user)
                .ifPresent(passwordResetTokenRepository::delete);

        // Créer un nouveau token (expire dans 1 heure)
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(resetToken);

        // Construire le lien de réinitialisation
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Envoyer l'email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getPrenom(), resetLink);

        System.out.println("✅ Email de réinitialisation envoyé à: " + email);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        System.out.println("🔐 Tentative de réinitialisation avec token");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Le lien a expiré. Veuillez faire une nouvelle demande.");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Ce lien a déjà été utilisé.");
        }

        // Mettre à jour le mot de passe
        User user = resetToken.getUser();
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        System.out.println("✅ Mot de passe réinitialisé pour: " + user.getEmail());
    }

    public boolean validateResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isUsed())
                .orElse(false);
    }

    // ==================== EMAIL VERIFICATION ====================

    @Transactional
    public void sendVerificationEmail(User user) {
        System.out.println("📧 Envoi email de vérification pour: " + user.getEmail());

        // Générer un token unique
        String token = UUID.randomUUID().toString();

        // Supprimer les anciens tokens non confirmés
        emailVerificationTokenRepository.findByUserAndConfirmedFalse(user)
                .ifPresent(emailVerificationTokenRepository::delete);

        // Créer un nouveau token (expire dans 24 heures)
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                token,
                user,
                LocalDateTime.now().plusHours(24)
        );
        emailVerificationTokenRepository.save(verificationToken);

        // Construire le lien de vérification
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        // Envoyer l'email
        emailService.sendVerificationEmail(user.getEmail(), user.getPrenom(), verificationLink);

        System.out.println("✅ Email de vérification envoyé à: " + user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) {
        System.out.println("🔐 Tentative de vérification avec token");

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Le lien a expiré. Veuillez demander un nouveau lien.");
        }

        if (verificationToken.isConfirmed()) {
            throw new RuntimeException("Cet email a déjà été vérifié.");
        }

        // Marquer l'email comme vérifié
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Marquer le token comme confirmé
        verificationToken.setConfirmed(true);
        emailVerificationTokenRepository.save(verificationToken);

        System.out.println("✅ Email vérifié pour: " + user.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Cet email est déjà vérifié.");
        }

        sendVerificationEmail(user);
    }
}
