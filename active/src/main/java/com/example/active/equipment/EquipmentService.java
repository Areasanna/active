package com.example.active.equipment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public Page<EquipmentResponse> list(Pageable pageable) {
        return equipmentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private EquipmentResponse toResponse(Equipment e) {
        return new EquipmentResponse(e.getId(), e.getName());
    }

    public EquipmentResponse create(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.name());

        equipmentRepository.save(equipment);

        return new EquipmentResponse(equipment.getId(), equipment.getName());
    }
}
