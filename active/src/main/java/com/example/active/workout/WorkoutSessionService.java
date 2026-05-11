package com.example.active.workout;

import com.example.active.exercise.ExerciseRepository;
import com.example.active.training.TrainingPlanDayRepository;
import com.example.active.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSessionCreateResponse.WorkoutSessionResponse registerSession(WorkoutSessionCreateRequest.Create req, User user) {
        var trainingDay = trainingPlanDayRepository.findById(req.trainingPlanDayId())
                .orElseThrow(() -> new RuntimeException("Dia do plano não encontrado"));

        var session = new WorkoutSession();
        session.setUser(user);
        session.setTrainingPlanDay(trainingDay);
        session.setDate(req.date());

        for (var exReq : req.exercises()) {
            var workoutEx = new WorkoutExercise();
            workoutEx.setWorkoutSession(session);
            workoutEx.setExercise(exerciseRepository.findById(exReq.exerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercício não encontrado")));

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
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado ou acesso negado"));
    }

    public Page<WorkoutSessionCreateResponse.WorkoutSessionResponse> list(User user, Pageable pageable) {
        return workoutSessionRepository.findAllByUserIdOrderByDateDesc(user.getId(), pageable)
                .map(this::toResponse);
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

