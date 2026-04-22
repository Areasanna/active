package com.example.active.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {
    // nome, email, peso, idade, nivel, altura, tempo de treino
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

   // @Enumerated(EnumType.STRING)
   // private TrainingLevel trainingLevel;
   // private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer idade;

    //Usuario informar o seu peso até 5 dígitos, sendo 2 após a vírgula
    @Column(nullable = false, precision = 5, scale = 2)
    @Positive(message = "O peso deve ser maior que zero")
    private BigDecimal peso;

    @Column(nullable = false, precision = 3, scale = 2)
    private Double altura;

    public Usuario(String name, String email, String password,
                   Integer idade, BigDecimal peso, Double altura) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }
}

