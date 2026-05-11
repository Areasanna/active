package com.example.active.exercicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {

    List<Exercise> findByCategory(ExerciseCategory category);

    List<Exercise> findByTitleContainingIgnoreCase(String title);
}
