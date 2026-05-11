package com.example.active.exercise;

import com.example.active.equipment.EquipmentRepository;
import com.example.active.equipment.EquipmentResponse;
import com.example.active.muscle.MuscleRepository;
import com.example.active.muscle.MuscleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final EquipmentRepository equipmentRepository;
    private final MuscleRepository muscleRepository;

    public ExerciseResponse create(ExerciseCreateRequest req) {
        validateCategory(req.category());
        Exercise exercise = new Exercise();
        populateExerciseFromDto(exercise, req.title(), req.description(), req.videoUrl(), req.category(),
                req.equipmentIds(), req.primaryMuscleIds(), req.secondaryMuscleIds());
        exerciseRepository.save(exercise);
        return toResponse(exercise);
    }

    public ExerciseResponse update(Long id, ExerciseUpdateRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));
        validateCategory(request.category());
        populateExerciseFromDto(exercise, request.title(), request.description(),
                request.videoUrl(), request.category(), request.equipmentIds(),
                request.primaryMuscleIds(), request.secondaryMuscleIds());
        exerciseRepository.save(exercise);
        return toResponse(exercise);
    }

    public void delete(Long id) {
        if (!exerciseRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado");
        exerciseRepository.deleteById(id);
    }

    public Page<ExerciseResponse> list(ExerciseCategory category, Long equipmentId, Long muscleId, Pageable pageable) {

        var spec = ExerciseSpecs.filterExercises(category, equipmentId, muscleId);

        Page<Exercise> exercisesPage = exerciseRepository.findAll(spec, pageable);

        return exercisesPage.map(this::toResponse);
    }

    public ExerciseResponse findById(Long id) {
        var e = exerciseRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));
        return toResponse(e);
    }

    //Verifica se a categoria enviada pelo usuario é nula.
    private void validateCategory(ExerciseCategory category) {
        if (category == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria inválida");
    }
    //transforma os dados brutos que vêm da API em objetos reais do banco de dados.
    private void populateExerciseFromDto(
            Exercise exercise, String title, String desc, String videoUrl,
            ExerciseCategory category, List<Long> equipmentIds,
            List<Long> primaryMuscleIds, List<Long> secondaryMuscleIds) {

        equipmentIds = equipmentIds == null ? List.of() : equipmentIds;
        primaryMuscleIds = primaryMuscleIds == null ? List.of() : primaryMuscleIds;

        exercise.setTitle(title);
        exercise.setDescription(desc);
        exercise.setVideoUrl(videoUrl);
        exercise.setCategory(category);

        exercise.setEquipment(new HashSet<>(equipmentRepository.findAllById(equipmentIds)));
        exercise.setPrimaryMuscles(new HashSet<>(muscleRepository.findAllById(primaryMuscleIds)));

        if (secondaryMuscleIds != null && !secondaryMuscleIds.isEmpty())
            exercise.setSecondaryMuscles(new HashSet<>(muscleRepository.findAllById(secondaryMuscleIds)));
        else
            exercise.setSecondaryMuscles(new HashSet<>());

        if (exercise.getEquipment().isEmpty() || exercise.getPrimaryMuscles().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deve possuir ao menos um equipamento e músculo primário");
    }
    //faz o mapeamento Entidade para DTO
    private ExerciseResponse toResponse(Exercise e) {
        return new ExerciseResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getVideoUrl(),
                e.getCategory(),
                e.getEquipment().stream()
                        .map(eq -> new EquipmentResponse(eq.getId(), eq.getName())).toList(),
                e.getPrimaryMuscles().stream()
                        .map(m -> new MuscleResponse(m.getId(), m.getName())).toList(),
                e.getSecondaryMuscles().stream()
                        .map(m -> new MuscleResponse(m.getId(), m.getName())).toList()
        );
    }
}