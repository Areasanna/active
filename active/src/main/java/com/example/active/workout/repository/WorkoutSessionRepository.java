package com.example.active.workout.repository;

import com.example.active.workout.model.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long>, JpaSpecificationExecutor<WorkoutSession> {

    Page<WorkoutSession> findAllByUserIdOrderByDateDesc(Long userId, Pageable pageable);
   //consulta a tela de detalhes do treino, onde você precisa exibir cada exercício e
   // cada série realizada sem travar a aplicação com dezenas de consultas
   // ao banco de dados.
    @Query("""
                SELECT DISTINCT ws FROM WorkoutSession ws
                LEFT JOIN FETCH ws.exercises se
                LEFT JOIN FETCH se.exercise
                LEFT JOIN FETCH se.sets
                WHERE ws.id = :id AND ws.user.id = :userId
            """)
    Optional<WorkoutSession> findByIdAndUserIdWithDetails(@Param("id") Long id, @Param("userId") Long userId);
}
