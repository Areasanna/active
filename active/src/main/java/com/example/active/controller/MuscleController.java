package com.example.active.controller;

import com.example.active.service.MuscleService;
import com.example.active.dto.MuscleRequest;
import com.example.active.dto.MuscleResponse;
import com.example.active.model.Muscle;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/muscles")
@RequiredArgsConstructor
public class MuscleController {
    private final MuscleService service;
    private static final Logger logger = LoggerFactory.getLogger(MuscleController.class);

    @GetMapping
    public ResponseEntity<Page<MuscleResponse>> list(
            @Parameter(hidden = true)
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "nameEn", spec = LikeIgnoreCase.class)
            }) Specification<Muscle> spec,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Get/musculos - Paginação: {}", pageable);

        return ResponseEntity.ok(service.list(spec, pageable));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MuscleResponse> create(@RequestBody @Valid MuscleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}

