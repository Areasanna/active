package com.example.active.workout;

import com.example.active.user.model.User;
import com.example.active.workout.dto.WorkoutSessionCreateRequest;
import com.example.active.workout.dto.WorkoutSessionCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {
    private final WorkoutSessionService service;

    @PostMapping
    public ResponseEntity<WorkoutSessionCreateResponse.WorkoutSessionResponse> register(
            @Valid @RequestBody WorkoutSessionCreateRequest.Create req,
            @AuthenticationPrincipal User userAutenticado) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerSession(req, userAutenticado));
    }

    @GetMapping
    public ResponseEntity<Page<WorkoutSessionCreateResponse.WorkoutSessionResponse>> list(
            @AuthenticationPrincipal User userAutenticado,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        return ResponseEntity.ok(service.list(userAutenticado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionCreateResponse.WorkoutSessionResponse> detail(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAutenticado) {

        return ResponseEntity.ok(service.detail(id, userAutenticado));
    }
}
