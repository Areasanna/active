package com.example.active.workout.repository;

import com.example.active.workout.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
    // servem para extrair as melhores marcas históricas de
    // um usuário em um exercício específico
    // Extrai o peso máximo histórico
    @Query("""
                SELECT MAX(ws.weightKg)
                FROM WorkoutSet ws
                JOIN ws.workoutExercise we
                JOIN we.workoutSession sess
                WHERE sess.user.id = :userId
                  AND we.exercise.id = :exerciseId
            """)
    Optional<BigDecimal> findMaxWeightByUserAndExercise(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId);

    // Extrai o recorde de repetições
    @Query("""
                SELECT MAX(ws.reps)
                FROM WorkoutSet ws
                JOIN ws.workoutExercise we
                JOIN we.workoutSession sess
                WHERE sess.user.id = :userId
                  AND we.exercise.id = :exerciseId
            """)
    Optional<Integer> findMaxRepsByUserAndExercise(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId);

    // Data em que o recorde de peso foi atingido
    @Query("""
                SELECT sess.date
                FROM WorkoutSet ws
                JOIN ws.workoutExercise we
                JOIN we.workoutSession sess
                WHERE sess.user.id = :userId
                  AND we.exercise.id = :exerciseId
                  AND ws.weightKg = :maxWeight
                ORDER BY sess.date DESC
            """)
    Optional<LocalDate> findDateOfMaxWeight(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId,
            @Param("maxWeight") BigDecimal maxWeight);
}
