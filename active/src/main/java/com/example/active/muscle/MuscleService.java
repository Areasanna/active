package com.example.active.muscle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MuscleService {
    private final MuscleRepository repository;

    public List<MuscleResponse> list() {
        return repository.findAll().stream()
                .map(m -> new MuscleResponse(m.getId(), m.getName(), m.getNameEn()))
                .toList();
    }
}
