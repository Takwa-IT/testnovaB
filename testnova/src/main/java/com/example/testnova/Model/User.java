package com.example.testnova.Model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class User extends Compte {

        private LocalDate dateInscription = LocalDate.now();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Cvanalyse> mescvsAnalyses; // Liste des CV associés
    }


