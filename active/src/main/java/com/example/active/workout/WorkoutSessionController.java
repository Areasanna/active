package com.example.active.workout;

import com.example.active.training.TrainingPlanController;
import com.example.active.user.model.User;
import com.example.active.workout.dto.WorkoutSessionCreateRequest;
import com.example.active.workout.dto.WorkoutSessionCreateResponse;
import com.example.active.workout.model.WorkoutSession;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private static final Logger logger = LoggerFactory.getLogger(WorkoutSessionController.class);

    @PostMapping
    public ResponseEntity<WorkoutSessionCreateResponse.WorkoutSessionResponse> register(
            @Valid @RequestBody WorkoutSessionCreateRequest.Create req,
            @AuthenticationPrincipal User userAutenticado) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerSession(req, userAutenticado));
    }

    @GetMapping
    public ResponseEntity<Page<WorkoutSessionCreateResponse.WorkoutSessionResponse>> list(
            @AuthenticationPrincipal User userAutenticado,
            @Parameter(hidden = true)
            @And({
                    @Spec(path = "date", params = {"fromDate", "toDate"}, spec = Between.class),
                    @Spec(path = "workoutName", spec = LikeIgnoreCase.class)
            }) Specification<WorkoutSession> spec,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        logger.info("Get/sessoes-treino - Usuário: {} - Paginação: {}", userAutenticado.getId(), pageable);

        return ResponseEntity.ok(service.list(userAutenticado, spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionCreateResponse.WorkoutSessionResponse> detail(
            @PathVariable Long id,
            @AuthenticationPrincipal User userAutenticado) {

        return ResponseEntity.ok(service.detail(id, userAutenticado));
    }
}
