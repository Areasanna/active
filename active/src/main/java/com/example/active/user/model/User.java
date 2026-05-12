package com.example.active.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private TrainingLevel trainingLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer age;

    //Usuario informar o seu peso até 5 dígitos, sendo 2 após a vírgula
    @Column(nullable = false, precision = 5, scale = 2)
    @Positive(message = "O peso deve ser maior que zero")
    private BigDecimal weight;

    @Column(nullable = false, precision = 3, scale = 2)
    @Positive(message = "A altura deve ser maior que zero")
    private BigDecimal height;
}

