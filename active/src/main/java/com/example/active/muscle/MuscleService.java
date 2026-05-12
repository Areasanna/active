package com.example.active.muscle;

import com.example.active.muscle.dto.MuscleRequest;
import com.example.active.muscle.dto.MuscleResponse;
import com.example.active.muscle.model.Muscle;
import com.example.active.muscle.repository.MuscleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MuscleService {
    private final MuscleRepository repository;

    public Page<MuscleResponse> list(Specification<Muscle> spec, Pageable pageable) {
        Page<Muscle> page = repository.findAll(spec, pageable);

        if (page.isEmpty()) {
            throw new EntityNotFoundException("Nenhum músculo encontrado.");
        }

        return page.map(m -> new MuscleResponse(m.getId(), m.getName(), m.getNameEn()));
    }

    public MuscleResponse create(MuscleRequest request) {
        validateMuscleRequest(request);
        var muscle = Muscle.builder()
                .name(request.name())
                .build();
        repository.save(muscle);
        return new MuscleResponse(muscle.getId(), muscle.getName(), muscle.getNameEn());
    }

    private void validateMuscleRequest(MuscleRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do músculo é obrigatório");
        }
    }
}
