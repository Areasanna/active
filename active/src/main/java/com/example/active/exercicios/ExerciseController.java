package com.example.active.exercicios;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> updateById(@PathVariable Long id, @Valid @RequestBody ExerciseUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ExerciseResponse> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long muscleId) {
        return ResponseEntity.ok(service.list(category, equipmentId, muscleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
