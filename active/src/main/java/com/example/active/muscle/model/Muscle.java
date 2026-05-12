package com.example.active.muscle.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "muscles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Muscle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_en", nullable = false)
    private String nameEn;
}
