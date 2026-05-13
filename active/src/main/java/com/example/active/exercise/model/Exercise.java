package com.example.active.exercise.model;

import com.example.active.equipment.model.Equipment;
import com.example.active.muscle.model.Muscle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseCategory category;

    @ManyToMany
    @JoinTable(name = "exercise_equipment")
    private Set<Equipment> equipment = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "exercise_primary_muscles",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "muscle_id"))
    private Set<Muscle> primaryMuscles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "exercise_secondary_muscles",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "muscle_id"))
    private Set<Muscle> secondaryMuscles = new HashSet<>();
}