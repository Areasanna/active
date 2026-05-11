package com.example.active.exercise;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;

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
            @RequestParam(required = false) ExerciseCategory category,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long muscleId, Pageable pageable) {
        return ResponseEntity.ok(service.list(category, equipmentId, muscleId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
