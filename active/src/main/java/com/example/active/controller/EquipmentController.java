package com.example.active.controller;

import com.example.active.service.EquipmentService;
import com.example.active.dto.EquipmentRequest;
import com.example.active.dto.EquipmentResponse;
import com.example.active.model.Equipment;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService service;
    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    @GetMapping
    public ResponseEntity<Page<EquipmentResponse>> list(
            @Parameter(hidden = true)
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "id", spec = Equal.class)
            }) Specification<Equipment> spec,
            @PageableDefault(size = 10) Pageable pageable) {

        logger.info("Get/equipamentos - Paginação: {}", pageable);

        return ResponseEntity.ok(service.list(spec, pageable));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@RequestBody @Valid EquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}

