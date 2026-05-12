package com.example.active.training.repository;

import com.example.active.training.model.TrainingPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long>, JpaSpecificationExecutor<TrainingPlan> {
    Page<TrainingPlan> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("""
                SELECT DISTINCT tp FROM TrainingPlan tp
                LEFT JOIN FETCH tp.weeks w
                LEFT JOIN FETCH w.days d
                LEFT JOIN FETCH d.exercises es
                LEFT JOIN FETCH es.exercise
                WHERE tp.id = :id AND tp.user.id = :userId
            """)
    Optional<TrainingPlan> findByIdAndUserIdWithDetails(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
