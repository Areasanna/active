package com.example.active.workout;

import com.example.active.exercise.repository.ExerciseRepository;
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

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSessionCreateResponse.WorkoutSessionResponse registerSession(WorkoutSessionCreateRequest.Create req, User user) {
        var trainingDay = trainingPlanDayRepository.findById(req.trainingPlanDayId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dia do plano não encontrado"));

        var session = new WorkoutSession();
        session.setUser(user);
        session.setTrainingPlanDay(trainingDay);
        session.setDate(req.date());

        if (req.exercises() == null || req.exercises().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A sessão de treino deve conter pelo menos um exercício");
        }

        for (var exReq : req.exercises()) {
            var workoutEx = new WorkoutExercise();
            workoutEx.setWorkoutSession(session);
            workoutEx.setExercise(exerciseRepository.findById(exReq.exerciseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado")));

            for (var setReq : exReq.sets()) {
                var set = new WorkoutSet();
                set.setWorkoutExercise(workoutEx);
                set.setReps(setReq.reps());
                set.setWeightKg(setReq.weightKg());
                workoutEx.getSets().add(set);
            }
            session.getExercises().add(workoutEx);
        }

        var savedSession = workoutSessionRepository.save(session);
        return toResponse(savedSession);
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

