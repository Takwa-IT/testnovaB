// src/main/java/com/example/testnova/Controller/AuthController.java
package com.example.testnova.Controller;

import com.example.testnova.Dto.JwtResponse;
import com.example.testnova.Dto.LoginRequest;
import com.example.testnova.Dto.RegisterRequest;
import com.example.testnova.Model.User;
import com.example.testnova.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}