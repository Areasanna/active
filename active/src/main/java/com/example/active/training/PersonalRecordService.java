package com.example.active.training;

import com.example.active.exercise.repository.ExerciseRepository;
import com.example.active.training.dto.PersonalRecordResponse;
import com.example.active.workout.repository.WorkoutSessionRepository;
import com.example.active.workout.repository.WorkoutSetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonalRecordService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    @Transactional
    public PersonalRecordResponse getPersonalRecord(Long userId, Long exerciseId) {
        var exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado"));


        BigDecimal maxWeightKg = workoutSetRepository.findMaxWeightByUserAndExercise(userId, exerciseId)
                .orElse(BigDecimal.ZERO);

        int maxReps = workoutSetRepository.findMaxRepsByUserAndExercise(userId, exerciseId)
                .orElse(0);

        LocalDate achievedAt = null;
        if (maxWeightKg.compareTo(BigDecimal.ZERO) > 0) {
            achievedAt = workoutSetRepository.findDateOfMaxWeight(userId, exerciseId, maxWeightKg)
                    .orElse(null);
        }

        return new PersonalRecordResponse(
                exerciseId,
                exercise.getTitle(),
                maxWeightKg,
                maxReps,
                achievedAt
        );
    }
}
