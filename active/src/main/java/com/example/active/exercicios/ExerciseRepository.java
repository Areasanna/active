package com.example.active.exercicios;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByCategory(Category category);

    List<Exercicio> findByTitleContainingIgnoreCase(String title);
}
