package com.example.testnova.Repository;

import com.example.testnova.Model.Cvanalyse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface cvanalyseRep extends JpaRepository<Cvanalyse, Long> {
    Cvanalyse getCvanalyseById(long id);

    // Chercher par l'id de l'objet User associé
    List<Cvanalyse> findAllByUser_Id(Long userId);

    // Corrected method name so Spring Data can resolve the property path 'user.id'
    Cvanalyse getCvanalyseByUser_Id(Long userId);
}
