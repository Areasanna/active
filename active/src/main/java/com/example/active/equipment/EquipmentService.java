package com.example.active.equipment;

import com.example.active.equipment.dto.EquipmentRequest;
import com.example.active.equipment.dto.EquipmentResponse;
import com.example.active.equipment.model.Equipment;
import com.example.active.equipment.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> list(Specification<Equipment> spec, Pageable pageable) {
        Page<Equipment> page = equipmentRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            throw new EntityNotFoundException("Nenhum equipamento encontrado para os parâmetros fornecidos.");
        }

        return page.map(e -> new EquipmentResponse(e.getId(), e.getName()));
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
