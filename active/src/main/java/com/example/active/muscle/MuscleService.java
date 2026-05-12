package com.example.active.muscle;

import com.example.active.muscle.dto.MuscleRequest;
import com.example.active.muscle.dto.MuscleResponse;
import com.example.active.muscle.model.Muscle;
import com.example.active.muscle.repository.MuscleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MuscleService {
    private final MuscleRepository repository;

    public Page<MuscleResponse> list(Pageable pageable) {
        return repository.findAll(pageable)
                .map(m -> new MuscleResponse(m.getId(), m.getName(), m.getNameEn()));
    }

    public MuscleResponse create(MuscleRequest request) {
        var muscle = Muscle.builder()
                .name(request.name())
                .build();
        repository.save(muscle);
        return new MuscleResponse(muscle.getId(), muscle.getName(), muscle.getNameEn());
    }
}
