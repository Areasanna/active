package com.example.active.training.repository;

import com.example.active.training.model.TrainingPlanDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainingPlanDayRepository extends JpaRepository<TrainingPlanDay, Long> {
   //A consulta esta verificando a hierarquia dos seus dados para garantir a
   // segurança, para garantir que um usuario só acesse os dias do seu próprio plano):
    @Query("""
                SELECT d FROM TrainingPlanDay d
                JOIN d.trainingPlanWeek w
                JOIN w.trainingPlan tp
                WHERE d.id = :dayId AND tp.user.id = :userId
            """)
    Optional<TrainingPlanDay> findByIdAndUserId(@Param("dayId") Long dayId, @Param("userId") Long userId);
}
