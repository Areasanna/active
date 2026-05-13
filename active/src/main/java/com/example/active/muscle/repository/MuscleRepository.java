package com.example.active.muscle.repository;

import com.example.active.muscle.model.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MuscleRepository extends JpaRepository<Muscle, Long>, JpaSpecificationExecutor<Muscle> {
}
