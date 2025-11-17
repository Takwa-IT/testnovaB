// src/main/java/com/example/testnova/Repository/RoleRepository.java
package com.example.testnova.Repository;

import com.example.testnova.Model.ERole;
import com.example.testnova.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}