package com.example.testnova.Config; // ou le package approprié

import com.example.testnova.Model.ERole;
import com.example.testnova.Model.Role;
import com.example.testnova.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if (roleRepository.findByName(ERole.ROLE_CANDIDAT).isEmpty()) {
            Role candidatRole = new Role(ERole.ROLE_CANDIDAT);
            roleRepository.save(candidatRole);
            System.out.println("Role ROLE_CANDIDAT created");
        }

        if (roleRepository.findByName(ERole.ROLE_HR).isEmpty()) {
            Role hrRole = new Role(ERole.ROLE_HR);
            roleRepository.save(hrRole);
            System.out.println("Role ROLE_HR created");
        }
    }
}