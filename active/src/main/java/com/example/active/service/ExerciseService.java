package com.example.active.service;

import com.example.active.dto.EquipmentResponse;
import com.example.active.repository.EquipmentRepository;
import com.example.active.dto.ExerciseCreateRequest;
import com.example.active.dto.ExerciseResponse;
import com.example.active.dto.ExerciseUpdateRequest;
import com.example.active.model.Exercise;
import com.example.active.model.ExerciseCategory;
import com.example.active.repository.ExerciseRepository;
import com.example.active.dto.MuscleResponse;
import com.example.active.repository.MuscleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final EquipmentRepository equipmentRepository;
    private final MuscleRepository muscleRepository;

    @Transactional
    public ExerciseResponse create(ExerciseCreateRequest req) {
        Exercise exercise = new Exercise();
        populateExerciseFromDto(exercise, req.title(), req.description(), req.videoUrl(), req.category(),
                req.equipmentIds(), req.primaryMuscleIds(), req.secondaryMuscleIds());

        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public ExerciseResponse update(Long id, ExerciseUpdateRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        populateExerciseFromDto(exercise, request.title(), request.description(),
                request.videoUrl(), request.category(), request.equipmentIds(),
                request.primaryMuscleIds(), request.secondaryMuscleIds());

        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public void delete(Long id) {
        if (!exerciseRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado");
        exerciseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ExerciseResponse> list(Specification<Exercise> spec, Pageable pageable) {
        Page<Exercise> exercisesPage = exerciseRepository.findAll(spec, pageable);
        return exercisesPage.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ExerciseResponse findById(Long id) {
        return exerciseRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));
    }

    private void populateExerciseFromDto(
            Exercise exercise, String title, String desc, String videoUrl,
            ExerciseCategory category, List<Long> equipmentIds,
            List<Long> primaryMuscleIds, List<Long> secondaryMuscleIds) {

        exercise.setTitle(title);
        exercise.setDescription(desc);
        exercise.setVideoUrl(videoUrl);
        exercise.setCategory(category);

        // Validação de Equipamentos
        var equipments = equipmentRepository.findAllById(equipmentIds != null ? equipmentIds : List.of());
        if (equipmentIds != null && equipments.size() != equipmentIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de equipamentos são inválidos");
        }
        exercise.setEquipment(new HashSet<>(equipments));

        // Validação de Músculos Primários
        var primary = muscleRepository.findAllById(primaryMuscleIds != null ? primaryMuscleIds : List.of());
        if (primaryMuscleIds != null && primary.size() != primaryMuscleIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de músculos primários são inválidos");
        }
        exercise.setPrimaryMuscles(new HashSet<>(primary));

        // Músculos Secundários (Opcionais)
        if (secondaryMuscleIds != null && !secondaryMuscleIds.isEmpty()) {
            var secondary = muscleRepository.findAllById(secondaryMuscleIds);
            if (secondary.size() != secondaryMuscleIds.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de músculos secundários são inválidos");
            }
            exercise.setSecondaryMuscles(new HashSet<>(secondary));
        } else {
            exercise.setSecondaryMuscles(new HashSet<>());
        }

        if (exercise.getEquipment().isEmpty() || exercise.getPrimaryMuscles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deve possuir ao menos um equipamento e músculo primário");
        }
    }

    private ExerciseResponse toResponse(Exercise e) {
        return new ExerciseResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getVideoUrl(),
                e.getCategory(),
                e.getEquipment().stream().map(eq -> new EquipmentResponse(eq.getId(), eq.getName())).toList(),
                e.getPrimaryMuscles().stream().map(m -> new MuscleResponse(m.getId(), m.getName(), m.getNameEn())).toList(),
                e.getSecondaryMuscles().stream().map(m -> new MuscleResponse(m.getId(), m.getName(), m.getNameEn())).toList()
        );
    }
}