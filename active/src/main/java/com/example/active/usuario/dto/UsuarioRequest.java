package com.example.active.usuario.dto;

import com.example.active.usuario.TrainingLevel;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class UsuarioRequest{
    @NotBlank(message = "O nome é obrigadorio")
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank(message = "E-mail é obrigadorio")
    @Email(message = "Informe um e-mail válido")
    private String email;

    @NotBlank(message = " A senha é obrigadoria")
    @Size(min = 8, message = "Senha tem que ter no mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$",
            message = "A senha deve ter letras e números")
    private String password;

    @NotNull(message = "O nível de treino é obrigatório")
    private TrainingLevel trainingLevel;

    @NotNull(message = "A idade é obrigatória")
    @Min(value = 14, message = "Idade mínima permitida é 14 anos")
    private Integer idade;

    @NotNull(message = "O peso é obrigatório")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal peso;

    @NotNull(message = "A altura é obrigatória")
    private Double altura;

    public UsuarioRequest(String nome, String email, String password, TrainingLevel trainingLevel, Integer idade, BigDecimal peso,
                          Double altura) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.trainingLevel = trainingLevel;
        this.idade = idade;
        this.peso = peso;
        this.altura = altura;
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

    public TrainingLevel getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(TrainingLevel trainingLevel) {
        this.trainingLevel = trainingLevel;
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


