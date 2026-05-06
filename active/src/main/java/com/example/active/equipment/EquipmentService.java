package com.example.active.equipment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository repository;

    public List<EquipmentResponse> list() {
        return repository.findAll().stream()
                .map(e -> new EquipmentResponse(e.getId(), e.getName()))
                .toList();
    }
}
