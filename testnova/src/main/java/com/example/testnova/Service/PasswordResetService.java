package com.example.testnova.Service;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.Compte;
import com.example.testnova.Model.EmailVerificationToken;
import com.example.testnova.Model.HR;
import com.example.testnova.Model.PasswordResetToken;
import com.example.testnova.Model.User;
import com.example.testnova.Repository.CandidatRepository;
import com.example.testnova.Repository.EmailVerificationTokenRepository;
import com.example.testnova.Repository.HRRepository;
import com.example.testnova.Repository.PasswordResetTokenRepository;
import com.example.testnova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;  // Rétrocompatibilité
    private final CandidatRepository candidatRepository;
    private final HRRepository hrRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                 CandidatRepository candidatRepository,
                                 HRRepository hrRepository,
                                 PasswordResetTokenRepository passwordResetTokenRepository,
                                 EmailVerificationTokenRepository emailVerificationTokenRepository,
                                 EmailService emailService,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.candidatRepository = candidatRepository;
        this.hrRepository = hrRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== FORGOT PASSWORD ====================

    @Transactional
    public void sendPasswordResetEmail(String email) {
        System.out.println("📧 Demande de réinitialisation pour: " + email);

        // Chercher dans Candidat, HR ou User (rétrocompatibilité)
        Compte compte = findCompteByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        // Générer un token unique
        String token = UUID.randomUUID().toString();

        // Supprimer les anciens tokens non utilisés
        passwordResetTokenRepository.findByCompteAndUsedFalse(compte)
                .ifPresent(passwordResetTokenRepository::delete);

        // Créer un nouveau token (expire dans 1 heure)
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                compte,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(resetToken);

        // Construire le lien de réinitialisation
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Envoyer l'email
        emailService.sendPasswordResetEmail(compte.getEmail(), compte.getPrenom(), resetLink);

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
        Compte compte = resetToken.getCompte();
        compte.setMotDePasse(passwordEncoder.encode(newPassword));
        
        // Sauvegarder selon le type
        if (compte instanceof Candidat) {
            candidatRepository.save((Candidat) compte);
        } else if (compte instanceof HR) {
            hrRepository.save((HR) compte);
        } else if (compte instanceof User) {
            userRepository.save((User) compte);
        }

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        System.out.println("✅ Mot de passe réinitialisé pour: " + compte.getEmail());
    }

    public boolean validateResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isUsed())
                .orElse(false);
    }

    // ==================== EMAIL VERIFICATION ====================

    @Transactional
    public void sendVerificationEmail(Compte compte) {
        System.out.println("📧 Envoi email de vérification pour: " + compte.getEmail());

        // Générer un token unique
        String token = UUID.randomUUID().toString();

        // Supprimer les anciens tokens non confirmés
        emailVerificationTokenRepository.findByCompteAndConfirmedFalse(compte)
                .ifPresent(emailVerificationTokenRepository::delete);

        // Créer un nouveau token (expire dans 24 heures)
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                token,
                compte,
                LocalDateTime.now().plusHours(24)
        );
        emailVerificationTokenRepository.save(verificationToken);

        // Construire le lien de vérification
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        // Envoyer l'email
        emailService.sendVerificationEmail(compte.getEmail(), compte.getPrenom(), verificationLink);

        System.out.println("✅ Email de vérification envoyé à: " + compte.getEmail());
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
        Compte compte = verificationToken.getCompte();
        
        // Mettre à jour selon le type
        if (compte instanceof Candidat) {
            Candidat candidat = (Candidat) compte;
            candidat.setEmailVerified(true);
            candidatRepository.save(candidat);
        } else if (compte instanceof HR) {
            HR hr = (HR) compte;
            hr.setEmailVerified(true);
            hrRepository.save(hr);
        } else if (compte instanceof User) {
            User user = (User) compte;
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        // Marquer le token comme confirmé
        verificationToken.setConfirmed(true);
        emailVerificationTokenRepository.save(verificationToken);

        System.out.println("✅ Email vérifié pour: " + compte.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        Compte compte = findCompteByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        // Vérifier si déjà vérifié selon le type
        boolean emailVerified = false;
        if (compte instanceof Candidat) {
            emailVerified = ((Candidat) compte).isEmailVerified();
        } else if (compte instanceof HR) {
            emailVerified = ((HR) compte).isEmailVerified();
        } else if (compte instanceof User) {
            emailVerified = ((User) compte).isEmailVerified();
        }

        if (emailVerified) {
            throw new RuntimeException("Cet email est déjà vérifié.");
        }

        sendVerificationEmail(compte);
    }

    /**
     * Méthode helper pour chercher un compte par email dans tous les repositories
     */
    private Optional<Compte> findCompteByEmail(String email) {
        // Chercher d'abord dans Candidat
        Optional<Candidat> candidat = candidatRepository.findByEmail(email);
        if (candidat.isPresent()) {
            return Optional.of(candidat.get());
        }

        // Puis dans HR
        Optional<HR> hr = hrRepository.findByEmail(email);
        if (hr.isPresent()) {
            return Optional.of(hr.get());
        }

        // Enfin dans User (rétrocompatibilité)
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return Optional.of(user.get());
        }

        return Optional.empty();
    }
}
