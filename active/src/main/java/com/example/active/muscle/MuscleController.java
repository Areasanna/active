package com.example.active.muscle;

import com.example.active.muscle.dto.MuscleRequest;
import com.example.active.muscle.dto.MuscleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/muscles")
@RequiredArgsConstructor
public class MuscleController {
    private final MuscleService service;

    @GetMapping
    public ResponseEntity<Page<MuscleResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PostMapping
    public ResponseEntity<MuscleResponse> create(@RequestBody @Valid MuscleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}

