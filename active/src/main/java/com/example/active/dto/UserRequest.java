package com.example.active.dto;

import com.example.active.model.Role;
import com.example.active.model.TrainingLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    private String email;

    @NotBlank(message = " A senha é obrigatória")
    @Size(min = 8, message = "Senha tem que ter no mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$",
            message = "A senha deve ter letras e números")
    private String password;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento tem que ser passada/verdadeira")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @NotNull(message = "O nível de treino é obrigatório")
    private TrainingLevel trainingLevel;

    @NotNull(message = "O peso é obrigatório")
    @Digits(integer = 3, fraction = 2)
    @DecimalMin(value = "1.0", message = "Peso deve ser maior que zero")
    private BigDecimal weight;

    @NotNull(message = "A altura é obrigatória")
    @Positive(message = "A altura deve ser maior que zero")
    private BigDecimal height;

    @NotNull
    Role role;
}


