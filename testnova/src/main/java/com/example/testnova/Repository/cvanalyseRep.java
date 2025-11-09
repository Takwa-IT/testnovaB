package com.example.testnova.Repository;

import com.example.testnova.Model.Cvanalyse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface cvanalyseRep extends JpaRepository<Cvanalyse, Long> {
    Cvanalyse getCvanalyseById(long id);


    List<Cvanalyse> findAllByUserId(Long user_id);

    // Corrected method name so Spring Data can resolve the property path 'user.id'
    Cvanalyse getCvanalyseByUserId(Long user_id);
}
