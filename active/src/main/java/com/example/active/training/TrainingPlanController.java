package com.example.active.training;

import com.example.active.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {
    private final TrainingPlanService service;

    @PostMapping
    public ResponseEntity<TrainingPlanCreateResponse> create(
            @Valid @RequestBody TrainingPlanCreateRequest req,
            @AuthenticationPrincipal User userAutenticado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, userAutenticado));
    }

    @GetMapping
    public ResponseEntity<Page<TrainingPlanCreateResponse>> list(@AuthenticationPrincipal User userAutenticado, Pageable pageable) {
        return ResponseEntity.ok(service.list(userAutenticado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanCreateResponse> get(@PathVariable Long id,
                                                    @AuthenticationPrincipal User userAutenticado) {
        return ResponseEntity.ok(service.detail(id, userAutenticado));
    }
}
