// src/main/java/com/example/testnova/Service/UserDetailsServiceImpl.java
package com.example.testnova.Service;

import com.example.testnova.Model.User;
import com.example.testnova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== 🔍 USER DETAILS SERVICE ===");
        System.out.println("📧 Recherche utilisateur avec email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ ERREUR: Utilisateur non trouvé avec email: " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email);
                });

        System.out.println("✅ Utilisateur trouvé en base:");
        System.out.println("   - ID: " + user.getId());
        System.out.println("   - Nom: " + user.getNom());
        System.out.println("   - Email: " + user.getEmail());
        System.out.println("   - Mot de passe hashé: " + (user.getMotDePasse() != null ? "[PRÉSENT]" : "[NULL]"));
        System.out.println("   - Rôles: " + user.getRoles().size());

        UserDetails userDetails = UserDetailsImpl.build(user);

        System.out.println("🔨 Conversion en UserDetailsImpl réussie");
        System.out.println("=== 🏁 FIN USER DETAILS SERVICE ===");

        return userDetails;
    }
}