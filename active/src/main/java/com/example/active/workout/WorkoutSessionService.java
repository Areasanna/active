package com.example.active.workout;

import com.example.active.exercise.model.Exercise;
import com.example.active.exercise.repository.ExerciseRepository;
import com.example.active.training.model.TrainingPlanDay;
import com.example.active.training.repository.TrainingPlanDayRepository;
import com.example.active.user.model.User;
import com.example.active.workout.dto.WorkoutSessionCreateRequest;
import com.example.active.workout.dto.WorkoutSessionCreateResponse;
import com.example.active.workout.model.WorkoutExercise;
import com.example.active.workout.model.WorkoutSession;
import com.example.active.workout.model.WorkoutSet;
import com.example.active.workout.repository.WorkoutSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSessionCreateResponse.WorkoutSessionResponse registerSession(WorkoutSessionCreateRequest.Create req, User user) {
        // 1. Busca opcional do dia do plano (Permite treinos avulsos)
        TrainingPlanDay trainingDay = null;
        if (req.trainingPlanDayId() != null) {
            trainingDay = trainingPlanDayRepository.findById(req.trainingPlanDayId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dia do plano não encontrado"));
        }

        // 2. Otimização de busca de exercícios (1 SELECT apenas)
        Set<Long> exIds = req.exercises().stream().map(e -> e.exerciseId()).collect(Collectors.toSet());
        Map<Long, Exercise> exerciseMap = exerciseRepository.findAllById(exIds).stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        var session = WorkoutSession.builder()
                .user(user)
                .trainingPlanDay(trainingDay)
                .date(req.date())
                .build();

        for (var exReq : req.exercises()) {
            Exercise exercise = exerciseMap.get(exReq.exerciseId());
            if (exercise == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício inválido");

            var workoutEx = WorkoutExercise.builder()
                    .workoutSession(session)
                    .exercise(exercise)
                    .sets(new ArrayList<>())
                    .build();

            for (var setReq : exReq.sets()) {
                workoutEx.getSets().add(WorkoutSet.builder()
                        .workoutExercise(workoutEx)
                        .reps(setReq.reps())
                        .weightKg(setReq.weightKg())
                        .build());
            }
            session.getExercises().add(workoutEx);
        }

        return toResponse(workoutSessionRepository.save(session));
    }
    @Transactional(readOnly = true)
    public WorkoutSessionCreateResponse.WorkoutSessionResponse detail(Long id, User user) {
        return workoutSessionRepository.findByIdAndUserIdWithDetails(id, user.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado ou acesso negado"));
    }

    @Transactional(readOnly = true)
    public Page<WorkoutSessionCreateResponse.WorkoutSessionResponse> list(
            User user,
            Specification<WorkoutSession> spec,
            Pageable pageable) {

        // Filtro obrigatório de segurança
        Specification<WorkoutSession> userSpec = (root, query, builder) ->
                builder.equal(root.get("user").get("id"), user.getId());

        // União: (Filtro do Usuario) AND (Filtros da URL)
        Page<WorkoutSession> page = workoutSessionRepository.findAll(
                Specification.where(userSpec).and(spec),
                pageable
        );

        if (page.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma sessão de treino encontrada.");
        }

        return page.map(this::toResponse);
    }

    private WorkoutSessionCreateResponse.WorkoutSessionResponse toResponse(WorkoutSession session) {
        return new WorkoutSessionCreateResponse.WorkoutSessionResponse(
                session.getId(),
                session.getTrainingPlanDay() != null ? session.getTrainingPlanDay().getId() : null,
                session.getDate(),
                session.getExercises().size(),
                session.getCreatedAt(),
                session.getExercises().stream()
                        .map(we -> new WorkoutSessionCreateResponse.WorkoutExerciseResponse(
                                we.getId(),
                                we.getExercise().getId(),
                                we.getExercise().getTitle(),
                                we.getSets().stream()
                                        .map(s -> new WorkoutSessionCreateResponse.SessionSetResponse(
                                                s.getId(), s.getReps(), s.getWeightKg()
                                        )).toList()
                        )).toList()
        );
    }
}

