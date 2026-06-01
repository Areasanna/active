package com.example.active.repository;

import com.example.active.model.Exercise;
import com.example.active.model.ExerciseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {
    // Retorna uma página de exercícios por categoria
    Page<Exercise> findByCategory(ExerciseCategory category, Pageable pageable);

    // Retorna uma página filtrando por parte do título
    Page<Exercise> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
