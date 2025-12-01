// src/main/java/com/example/testnova/Service/UserDetailsServiceImpl.java
package com.example.testnova.Service;

import com.example.testnova.Model.Candidat;
import com.example.testnova.Model.HR;
import com.example.testnova.Model.User;
import com.example.testnova.Repository.CandidatRepository;
import com.example.testnova.Repository.HRRepository;
import com.example.testnova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private HRRepository hrRepository;

    @Autowired
    private UserRepository userRepository;  // Rétrocompatibilité

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== 🔍 USER DETAILS SERVICE ===");
        System.out.println("📧 Recherche compte avec email: " + email);

        // Chercher dans Candidat
        Optional<Candidat> candidatOpt = candidatRepository.findByEmail(email);
        if (candidatOpt.isPresent()) {
            Candidat candidat = candidatOpt.get();
            System.out.println("✅ Candidat trouvé:");
            System.out.println("   - ID: " + candidat.getId());
            System.out.println("   - Nom: " + candidat.getNom());
            System.out.println("   - Email: " + candidat.getEmail());
            System.out.println("   - Rôle: ROLE_CANDIDAT (basé sur le type de classe)");
            
            UserDetails userDetails = UserDetailsImpl.build(candidat);
            System.out.println("🔨 Conversion en UserDetailsImpl réussie");
            System.out.println("=== 🏁 FIN USER DETAILS SERVICE ===");
            return userDetails;
        }

        // Chercher dans HR
        Optional<HR> hrOpt = hrRepository.findByEmail(email);
        if (hrOpt.isPresent()) {
            HR hr = hrOpt.get();
            System.out.println("✅ HR trouvé:");
            System.out.println("   - ID: " + hr.getId());
            System.out.println("   - Nom: " + hr.getNom());
            System.out.println("   - Email: " + hr.getEmail());
            System.out.println("   - Rôle: ROLE_HR (basé sur le type de classe)");
            
            UserDetails userDetails = UserDetailsImpl.build(hr);
            System.out.println("🔨 Conversion en UserDetailsImpl réussie");
            System.out.println("=== 🏁 FIN USER DETAILS SERVICE ===");
            return userDetails;
        }

        // Chercher dans User (rétrocompatibilité)
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ User trouvé (ancien système):");
            System.out.println("   - ID: " + user.getId());
            System.out.println("   - Nom: " + user.getNom());
            System.out.println("   - Email: " + user.getEmail());
            System.out.println("   - Rôles: " + user.getRoles().size());
            
            UserDetails userDetails = UserDetailsImpl.build(user);
            System.out.println("🔨 Conversion en UserDetailsImpl réussie");
            System.out.println("=== 🏁 FIN USER DETAILS SERVICE ===");
            return userDetails;
        }

        // Aucun compte trouvé
        System.out.println("❌ ERREUR: Aucun compte trouvé avec email: " + email);
        throw new UsernameNotFoundException("Aucun compte trouvé avec email: " + email);
    }
}