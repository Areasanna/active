package com.example.active.exercise;

import com.example.active.exercise.dto.ExerciseCreateRequest;
import com.example.active.exercise.dto.ExerciseResponse;
import com.example.active.exercise.dto.ExerciseUpdateRequest;
import com.example.active.exercise.model.Exercise;
import com.example.active.muscle.MuscleController;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@RequestBody @Valid ExerciseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> updateById(@PathVariable Long id, @Valid @RequestBody ExerciseUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ExerciseResponse>> list(
            @Parameter(hidden = true)
            @And({
                    @Spec(path = "category", spec = Equal.class),
                    @Spec(path = "equipment.id", params = "equipmentId", spec = Equal.class),
                    @Spec(path = "muscles.id", params = "muscleId", spec = Equal.class)
            }) Specification<Exercise> spec,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Get/exercicios - Paginação: {} - Filtros: {}", pageable, spec);

        return ResponseEntity.ok(service.list(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
