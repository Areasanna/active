package com.example.active.training;

import com.example.active.exercise.model.Exercise;
import com.example.active.exercise.repository.ExerciseRepository;
import com.example.active.training.dto.*;
import com.example.active.training.model.*;
import com.example.active.training.repository.TrainingPlanRepository;
import com.example.active.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public TrainingPlanCreateResponse create(TrainingPlanCreateRequest request, User user) {
        // Coleta todos os IDs de exercícios do request de uma vez
        Set<Long> allExerciseIds = request.weeks().stream()
                .flatMap(w -> w.days().stream())
                .flatMap(d -> d.exercises().stream())
                .map(ExerciseSlotRequest::exerciseId)
                .collect(Collectors.toSet());

        // Busca todos de uma vez e coloca num mapa para acesso rápido
        Map<Long, Exercise> exerciseMap = exerciseRepository.findAllById(allExerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        if (exerciseMap.size() != allExerciseIds.size()) {
            throw new RuntimeException("Um ou mais exercícios informados não existem");
        }

        TrainingPlan plan = TrainingPlan.builder()
                .name(request.name())
                .goal(request.goal())
                .weekCount(request.weekCount())
                .user(user)
                .weeks(new ArrayList<>())
                .build();

        // Montagem da hierarquia
        request.weeks().forEach(weekReq -> {
            TrainingPlanWeek week = TrainingPlanWeek.builder()
                    .weekNumber(weekReq.weekNumber())
                    .trainingPlan(plan)
                    .days(new ArrayList<>())
                    .build();
            plan.getWeeks().add(week);

            weekReq.days().forEach(dReq -> {
                TrainingPlanDay day = TrainingPlanDay.builder()
                        .dayOfWeek(dReq.dayOfWeek())
                        .splitFocus(dReq.splitFocus())
                        .trainingPlanWeek(week)
                        .exercises(new ArrayList<>())
                        .build();
                week.getDays().add(day);

                dReq.exercises().forEach(sReq -> {
                    ExerciseSlot slot = ExerciseSlot.builder()
                            .exercise(exerciseMap.get(sReq.exerciseId()))
                            .sets(sReq.sets())
                            .reps(sReq.reps())
                            .weightKg(sReq.weightKg())
                            .restSeconds(sReq.restSeconds())
                            .trainingPlanDay(day)
                            .build();
                    day.getExercises().add(slot);
                });
            });
        });

        return toResponse(trainingPlanRepository.save(plan));
    }


    @Transactional(readOnly = true)
    public Page<TrainingPlanCreateResponse> list(User user, Specification<TrainingPlan> spec, Pageable pageable) {

        // O plano deve pertencer ao usuario logado
        Specification<TrainingPlan> userSpec = (root, query, builder) ->
                builder.equal(root.get("user").get("id"), user.getId());

        // base com o filtro do usuario
        Specification<TrainingPlan> finalSpec = Specification.where(userSpec);

        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        Page<TrainingPlan> page = trainingPlanRepository.findAll(finalSpec, pageable);

        return page.map(this::toResponse);
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
