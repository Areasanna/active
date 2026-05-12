package com.example.active.training.model;

import com.example.active.exercise.model.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "exercise_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_plan_day_id", nullable = false)
    private TrainingPlanDay trainingPlanDay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    @Builder.Default
    private Integer sets = 0;

    @Column(nullable = false)
    private Integer reps;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "rest_seconds", nullable = false)
    @Builder.Default
    private Integer restSeconds = 0;
}
