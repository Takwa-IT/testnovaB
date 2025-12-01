// src/main/java/com/example/testnova/Service/AuthService.java
package com.example.testnova.Service;

import com.example.testnova.Config.JwtUtil;
import com.example.testnova.Dto.JwtResponse;
import com.example.testnova.Dto.LoginRequest;
import com.example.testnova.Dto.RegisterRequest;
import com.example.testnova.Model.ERole;
import com.example.testnova.Model.Role;
import com.example.testnova.Model.User;
import com.example.testnova.Repository.RoleRepository;
import com.example.testnova.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    public final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;

    public AuthService(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    // MODIFIEZ votre méthode authenticateUser dans AuthService :
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        System.out.println("=== 🚨 DÉBUT DÉBOGAGE LOGIN ===");
        System.out.println("📧 Email reçu: " + loginRequest.getEmail());

        try {
            // 🔥 SUPPRIMEZ la vérification manuelle - Laissez Spring Security faire son
            // travail
            // Spring Security va automatiquement appeler UserDetailsService et vérifier le
            // mot de passe

            System.out.println("🔄 Tentative d'authentification Spring Security...");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getMotDePasse()));

            System.out.println("✅ Authentification Spring Security réussie!");

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Génération du token JWT
            String jwt = jwtUtil.generateToken(authentication);
            System.out.println("🔐 Token JWT généré avec succès");

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            JwtResponse response = new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getNom(),
                    userDetails.getPrenom(),
                    roles,
                    userDetails.getTelephone(), // ← Ajouter
                    userDetails.getVille(), // ← Ajouter
                    userDetails.getPosteRecherche() // ← Ajouter
            );

            System.out.println("✅ ✅ ✅ LOGIN RÉUSSI!");
            System.out.println("=== 🏁 FIN DÉBOGAGE LOGIN ===");

            return response;

        } catch (BadCredentialsException e) {
            System.out.println("❌ BadCredentialsException: Email ou mot de passe incorrect");
            throw new RuntimeException("Email ou mot de passe incorrect");
        } catch (Exception e) {
            System.out.println("❌ Erreur d'authentification: " + e.getMessage());
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        System.out.println("=== 📝 DÉBUT INSCRIPTION ===");
        System.out.println("Données reçues:");
        System.out.println("   - Nom: " + registerRequest.getNom());
        System.out.println("   - Prénom: " + registerRequest.getPrenom());
        System.out.println("   - Email: " + registerRequest.getEmail());
        System.out.println("   - Rôle: " + registerRequest.getRole());
        System.out.println("   - Mot de passe: [PROTÉGÉ]");

        // Vérification de l'email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            System.out.println("❌ Email déjà utilisé: " + registerRequest.getEmail());
            throw new RuntimeException("Error: Email is already taken!");
        }
        System.out.println("✅ Email disponible");

        // Création de l'utilisateur
        User user = createUserFromRequest(registerRequest);

        // Attribution des rôles
        Set<Role> roles = determineUserRoles(registerRequest.getRole());
        user.setRoles(roles);

        System.out.println("🔄 Sauvegarde de l'utilisateur...");

        // Sauvegarde
        User savedUser = userRepository.save(user);

        System.out.println("✅ Utilisateur sauvegardé avec succès:");
        System.out.println("   - ID: " + savedUser.getId());
        System.out.println("   - Email: " + savedUser.getEmail());
        System.out.println("   - Mot de passe hashé: " + savedUser.getMotDePasse());
        System.out.println("   - Rôles attribués: " + savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(", ")));

        // Envoyer l'email de vérification
        try {
            passwordResetService.sendVerificationEmail(savedUser);
            System.out.println("📧 Email de vérification envoyé");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'envoi de l'email de vérification: " + e.getMessage());
            // On continue même si l'email échoue
        }

        System.out.println("=== 🏁 FIN INSCRIPTION ===");

        return savedUser;
    }

    /**
     * Crée un utilisateur à partir de la requête d'inscription
     */
    private User createUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());

        String rawPassword = request.getMotDePasse();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setMotDePasse(encodedPassword);

        System.out.println("🔑 Hachage du mot de passe:");
        System.out.println("   - Mot de passe original: " + rawPassword);
        System.out.println("   - Mot de passe hashé: " + encodedPassword);

        return user;
    }

    /**
     * Détermine les rôles de l'utilisateur en fonction de la requête
     */
    private Set<Role> determineUserRoles(String roleRequest) {
        Set<Role> roles = new HashSet<>();
        ERole roleEnum;

        if (roleRequest == null) {
            // Rôle par défaut
            roleEnum = ERole.ROLE_CANDIDAT;
            System.out.println("🎯 Attribution du rôle par défaut: " + roleEnum);
        } else {
            // Mapping des rôles depuis la requête
            switch (roleRequest.toUpperCase()) {
                case "HR":
                case "ROLE_HR":
                    roleEnum = ERole.ROLE_HR;
                    break;
                case "CANDIDAT":
                case "ROLE_CANDIDAT":
                default:
                    roleEnum = ERole.ROLE_CANDIDAT;
            }
            System.out.println("🎯 Rôle demandé: " + roleRequest + " → Rôle attribué: " + roleEnum);
        }

        // Récupération du rôle depuis la base de données
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> {
                    System.out.println("❌ Rôle non trouvé en base: " + roleEnum);
                    return new RuntimeException("Error: Role " + roleEnum + " is not found in database.");
                });

        System.out.println("✅ Rôle trouvé en base: " + role.getName());
        roles.add(role);
        return roles;
    }

    /**
     * 🔓 MÉTHODE DE DÉCODAGE/VÉRIFICATION DU MOT DE PASSE
     * Compare le mot de passe en clair avec le hash stocké (déhashage)
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        System.out.println("=== 🔓 DÉCODAGE MOT DE PASSE ===");
        System.out.println("🔑 Mot de passe en clair: " + rawPassword);
        System.out.println("🔒 Mot de passe hashé: " + encodedPassword);

        // Vérification que les paramètres ne sont pas null
        if (rawPassword == null || encodedPassword == null) {
            System.out.println("❌ ERREUR: Mot de passe null");
            return false;
        }

        // 🔓 DÉCODAGE: Comparaison du mot de passe en clair avec le hash
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        System.out.println("🔍 Résultat décodage: " + matches);

        if (matches) {
            System.out.println("🎉 SUCCÈS: Le mot de passe est correct après déhashage");
        } else {
            System.out.println("❌ ÉCHEC: Le mot de passe ne correspond pas après déhashage");

            // Debug supplémentaire
            System.out.println("🐛 Debug supplémentaire:");
            System.out.println("   - Longueur mdp clair: " + rawPassword.length());
            System.out.println("   - Longueur hash: " + encodedPassword.length());
            System.out.println("   - Hash commence par: " +
                    (encodedPassword.length() > 10 ? encodedPassword.substring(0, 10) + "..." : encodedPassword));
        }

        System.out.println("=== 🔓 FIN DÉCODAGE ===");
        return matches;
    }

    /**
     * 🔧 MÉTHODE DE DÉBOGAGE COMPLET DU PROCESSUS DE HACHAGE
     */
    public void debugPasswordHashing(String rawPassword, String storedHash) {
        System.out.println("=== 🐛 DÉBOGAGE COMPLET HACHAGE ===");
        System.out.println("📝 Mot de passe original: " + rawPassword);
        System.out.println("💾 Hash stocké en base: " + storedHash);

        if (rawPassword == null || storedHash == null) {
            System.out.println("❌ ERREUR: Paramètres null");
            return;
        }

        // Générer un nouveau hash pour comparer
        String newHash = passwordEncoder.encode(rawPassword);
        System.out.println("🆕 Nouveau hash généré: " + newHash);

        // Vérifier si le nouveau hash correspond au mot de passe original
        boolean newHashValid = passwordEncoder.matches(rawPassword, newHash);
        System.out.println("✅ Nouveau hash valide avec mdp original: " + newHashValid);

        // Vérifier si le hash stocké correspond au mot de passe original
        boolean storedHashValid = passwordEncoder.matches(rawPassword, storedHash);
        System.out.println("✅ Hash stocké valide avec mdp original: " + storedHashValid);

        // Vérifier si les deux hashs sont identiques (normalement non, car salt
        // différent)
        boolean hashesIdentical = newHash.equals(storedHash);
        System.out.println("🔍 Les deux hashs sont identiques: " + hashesIdentical);

        // Test avec plusieurs générations de hash
        System.out.println("🧪 Test multiple de hachage:");
        for (int i = 1; i <= 3; i++) {
            String testHash = passwordEncoder.encode(rawPassword);
            boolean testValid = passwordEncoder.matches(rawPassword, testHash);
            System.out.println("   Hash " + i + ": " + testHash.substring(0, 20) + "... → Valide: " + testValid);
        }

        System.out.println("=== 🐛 FIN DÉBOGAGE HACHAGE ===");
    }

    /**
     * 🔧 MÉTHODE UTILITAIRE POUR TESTER LE DÉCODAGE SANS LOGIN
     */
    public boolean testPasswordDecoding(String email, String rawPassword) {
        System.out.println("=== 🔧 TEST DÉCODAGE SANS LOGIN ===");
        System.out.println("📧 Email: " + email);
        System.out.println("🔑 Mot de passe test: " + rawPassword);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            System.out.println("❌ Utilisateur non trouvé");
            return false;
        }

        User user = userOpt.get();
        System.out.println("✅ Utilisateur trouvé: " + user.getEmail());

        // Utiliser la méthode de décodage
        boolean result = verifyPassword(rawPassword, user.getMotDePasse());

        System.out.println("🔧 Résultat test décodage: " + result);
        System.out.println("=== 🔧 FIN TEST DÉCODAGE ===");

        return result;
    }

}