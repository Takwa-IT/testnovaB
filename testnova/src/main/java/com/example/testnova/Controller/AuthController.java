// src/main/java/com/example/testnova/Controller/AuthController.java
package com.example.testnova.Controller;

import com.example.testnova.Dto.ChangePasswordRequest;
import com.example.testnova.Dto.ForgotPasswordRequest;
import com.example.testnova.Dto.JwtResponse;
import com.example.testnova.Dto.LoginRequest;
import com.example.testnova.Dto.RegisterRequest;
import com.example.testnova.Dto.ResetPasswordRequest;
import com.example.testnova.Dto.UpdateProfileRequest;
import com.example.testnova.Model.User;
import com.example.testnova.Service.AuthService;
import com.example.testnova.Service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email ou mot de passe incorrect"));
        }
    }

    // src/main/java/com/example/testnova/Controller/AuthController.java
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.registerUser(registerRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("user", Map.of(
                    "id", user.getId(),        // ✅ Direct getter
                    "email", user.getEmail(),  // ✅ Direct getter
                    "nom", user.getNom(),      // ✅ Direct getter
                    "prenom", user.getPrenom() // ✅ Direct getter
            ));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        // ✅ UTILISEZ la méthode getter au lieu d'accéder directement au repository
        boolean exists = authService.getUserRepository().existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getUserRepository().findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (request.getNom() != null) user.setNom(request.getNom());
            if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
            if (request.getVille() != null) user.setVille(request.getVille());
            if (request.getPosteRecherche() != null) user.setPosteRecherche(request.getPosteRecherche());

            authService.getUserRepository().save(user);

            return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getUserRepository().findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérifier l'ancien mot de passe
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getMotDePasse())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mot de passe actuel incorrect"));
            }

            // Mettre à jour avec le nouveau mot de passe
            user.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
            authService.getUserRepository().save(user);

            return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== FORGOT PASSWORD ====================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Un email de réinitialisation a été envoyé à " + request.getEmail()
            ));
        } catch (Exception e) {
            // Pour des raisons de sécurité, on renvoie toujours un succès
            // pour ne pas révéler si l'email existe ou non
            return ResponseEntity.ok(Map.of(
                    "message", "Si un compte existe avec cet email, vous recevrez un lien de réinitialisation."
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateResetToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    // ==================== EMAIL VERIFICATION ====================

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            passwordResetService.verifyEmail(token);
            return ResponseEntity.ok(Map.of("message", "Email vérifié avec succès! Vous pouvez maintenant vous connecter."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            passwordResetService.resendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "Un nouvel email de vérification a été envoyé."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}