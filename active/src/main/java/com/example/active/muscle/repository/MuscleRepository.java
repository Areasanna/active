package com.example.active.muscle.repository;

import com.example.active.muscle.model.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscleRepository extends JpaRepository<Muscle, Long> {
}
