package com.example.active.training;

import com.example.active.training.model.TrainingPlanWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainingPlanWeekRepository extends JpaRepository<TrainingPlanWeek, Long> {
    // Garante que o usuario só acesse uma semana que pertence ao plano DELE
    @Query("""
                SELECT w FROM TrainingPlanWeek w 
                JOIN w.trainingPlan tp 
                WHERE w.id = :weekId AND tp.user.id = :userId
            """)
    Optional<TrainingPlanWeek> findByIdAndUserId(Long weekId, Long userId);

    boolean existsByTrainingPlanIdAndWeekNumber(Long trainingPlanId, Integer weekNumber);

}
