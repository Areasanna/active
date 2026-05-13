package com.example.active.training;

import com.example.active.equipment.EquipmentController;
import com.example.active.training.dto.TrainingPlanCreateRequest;
import com.example.active.training.dto.TrainingPlanCreateResponse;
import com.example.active.training.model.TrainingPlan;
import com.example.active.user.model.User;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {
    private final TrainingPlanService service;
    private static final Logger logger = LoggerFactory.getLogger(TrainingPlanController.class);

    @PostMapping
    public ResponseEntity<TrainingPlanCreateResponse> create(
            @Valid @RequestBody TrainingPlanCreateRequest req,
            @AuthenticationPrincipal User userAutenticado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, userAutenticado));
    }

    @GetMapping
    public ResponseEntity<Page<TrainingPlanCreateResponse>> list(
            @AuthenticationPrincipal User userAutenticado,
            @Parameter(hidden = true)
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "goal", spec = Equal.class)
            }) Specification<TrainingPlan> spec,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Get/planos-treino - Usuário: {} - Paginação: {}", userAutenticado.getId(), pageable);

        return ResponseEntity.ok(service.list(userAutenticado, spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanCreateResponse> get(@PathVariable Long id,
                                                    @AuthenticationPrincipal User userAutenticado) {
        return ResponseEntity.ok(service.detail(id, userAutenticado));
    }
}
