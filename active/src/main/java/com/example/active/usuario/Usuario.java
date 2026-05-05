package com.example.active.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class Usuario {
    // nome, email, peso, idade, nivel, altura, tempo de treino
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

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
    private Integer idade;

    //Usuario informar o seu peso até 5 dígitos, sendo 2 após a vírgula
    @Column(nullable = false, precision = 5, scale = 2)
    @Positive(message = "O peso deve ser maior que zero")
    private BigDecimal peso;

    @Column(nullable = false, precision = 3, scale = 2)
    @Positive(message = "A altura deve ser maior que zero")
    private BigDecimal altura;

    public Usuario(){

    }

    public Usuario(String nome, String email, String password,
                   Integer idade, BigDecimal peso, BigDecimal altura, TrainingLevel trainingLevel) {

        this.nome = nome;
        this.email = email;
        this.password = password;
        this.idade = idade;
        this.peso = peso;
        this.altura = altura;
        this.trainingLevel = trainingLevel;
    }

    public TrainingLevel getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(TrainingLevel trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

