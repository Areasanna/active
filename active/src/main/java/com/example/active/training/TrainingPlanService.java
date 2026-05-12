package com.example.active.training;

import com.example.active.exercise.repository.ExerciseRepository;
import com.example.active.training.dto.TrainingPlanCreateRequest;
import com.example.active.training.dto.TrainingPlanCreateResponse;
import com.example.active.training.model.*;
import com.example.active.training.repository.TrainingPlanRepository;
import com.example.active.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public TrainingPlanCreateResponse create(TrainingPlanCreateRequest request, User user) { // Alterado para TrainingPlanResponse
        TrainingPlan plan = new TrainingPlan();
        plan.setName(request.name());
        plan.setGoal(request.goal());
        plan.setWeekCount(request.weekCount());
        plan.setUser(user);

        for (var weekReq : request.weeks()) {
            TrainingPlanWeek week = new TrainingPlanWeek();
            week.setWeekNumber(weekReq.weekNumber());
            week.setTrainingPlan(plan);
            plan.getWeeks().add(week);

            for (var dReq : weekReq.days()) {
                TrainingPlanDay day = new TrainingPlanDay();
                day.setDayOfWeek(dReq.dayOfWeek());
                day.setSplitFocus(dReq.splitFocus());
                day.setTrainingPlanWeek(week);
                week.getDays().add(day);

                for (var sReq : dReq.exercises()) {
                    ExerciseSlot slot = new ExerciseSlot();
                    slot.setExercise(exerciseRepository.findById(sReq.exerciseId())
                            .orElseThrow(() -> new RuntimeException("Exercício não encontrado: " + sReq.exerciseId())));
                    slot.setSets(sReq.sets());
                    slot.setReps(sReq.reps());
                    slot.setWeightKg(sReq.weightKg());
                    slot.setRestSeconds(sReq.restSeconds());
                    slot.setTrainingPlanDay(day);
                    day.getExercises().add(slot);
                }
            }
        }
        TrainingPlan savedPlan = trainingPlanRepository.save(plan);
        return toResponse(savedPlan);
    }


    @Transactional
    public Page<TrainingPlanCreateResponse> list(User user, Pageable pageable) {
        return trainingPlanRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public TrainingPlanCreateResponse detail(Long id, User user) {
        return trainingPlanRepository.findByIdAndUserIdWithDetails(id, user.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado ou acesso negado"));
    }

    private TrainingPlanCreateResponse toResponse(TrainingPlan plan) {
        return new TrainingPlanCreateResponse(
                plan.getId(),
                plan.getName(),
                plan.getGoal(),
                plan.getWeekCount(),
                plan.getCreatedAt(),
                plan.getWeeks().stream()
                        .map(week -> new TrainingPlanWeekResponse(
                                week.getId(),
                                week.getWeekNumber(),
                                week.getDays().stream()
                                        .map(day -> new TrainingPlanDayResponse(
                                                day.getId(),
                                                day.getDayOfWeek(),
                                                day.getSplitFocus(),
                                                day.getExercises().stream()
                                                        .map(slot -> new ExerciseSlotResponse(
                                                                slot.getId(),
                                                                slot.getExercise().getId(),
                                                                slot.getExercise().getTitle(),
                                                                slot.getSets(),
                                                                slot.getReps(),
                                                                slot.getWeightKg(),
                                                                slot.getRestSeconds()
                                                        )).toList()
                                        )).toList()
                        )).toList()
        );
    }
}
