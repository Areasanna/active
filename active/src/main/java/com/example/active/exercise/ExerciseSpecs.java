package com.example.active.exercise;

import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ExerciseSpecs {
    public static Specification<Exercise> filterExercises(ExerciseCategory category, Long equipmentId, Long muscleId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category));
            }

            if (equipmentId != null) {
                predicates.add(cb.equal(root.join("equipment").get("id"), equipmentId));
            }

            if (muscleId != null) {
                // Filtra se o ID está nos músculos primários OU secundários
                Predicate primary = cb.equal(root.join("primaryMuscles").get("id"), muscleId);
                Predicate secondary = cb.equal(root.join("secondaryMuscles").get("id"), muscleId);
                predicates.add(cb.or(primary, secondary));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
