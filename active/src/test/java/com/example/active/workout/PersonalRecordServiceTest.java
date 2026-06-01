package com.example.active.workout;

import com.example.active.model.Exercise;
import com.example.active.repository.ExerciseRepository;
import com.example.active.service.PersonalRecordService;
import com.example.active.dto.PersonalRecordResponse;
import com.example.active.repository.WorkoutSessionRepository;
import com.example.active.repository.WorkoutSetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalRecordServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @InjectMocks
    private PersonalRecordService personalRecordService;

    private Exercise mockExercise;
    private final Long userId = 1L;
    private final Long exerciseId = 50L;

    @BeforeEach
    void setUp() {
        mockExercise = new Exercise();
        mockExercise.setId(exerciseId);
        mockExercise.setTitle("Supino Reto");
    }

    @Nested
    @DisplayName("Testes do método getPersonalRecord")
    class GetPersonalRecordTests {

        @Test
        @DisplayName("Deve retornar o recorde pessoal completo quando o usuário possuir histórico com carga")
        void getPersonalRecord_WithFullHistory_ShouldReturnResponse() {
            // Arrange
            BigDecimal maxWeight = new BigDecimal("100.00");
            int maxReps = 12;
            LocalDate expectedDate = LocalDate.of(2026, 5, 1);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
            when(workoutSetRepository.findMaxWeightByUserAndExercise(userId, exerciseId)).thenReturn(Optional.of(maxWeight));
            when(workoutSetRepository.findMaxRepsByUserAndExercise(userId, exerciseId)).thenReturn(Optional.of(maxReps));
            when(workoutSetRepository.findDateOfMaxWeight(userId, exerciseId, maxWeight)).thenReturn(Optional.of(expectedDate));

            // Act
            PersonalRecordResponse response = personalRecordService.getPersonalRecord(userId, exerciseId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.exerciseId()).isEqualTo(exerciseId);
            assertThat(response.exerciseTitle()).isEqualTo("Supino Reto");
            assertThat(response.maxWeightKg()).isEqualTo(maxWeight);
            assertThat(response.maxReps()).isEqualTo(maxReps);
            assertThat(response.achievedAt()).isEqualTo(expectedDate);

            verify(exerciseRepository).findById(exerciseId);
            verify(workoutSetRepository).findMaxWeightByUserAndExercise(userId, exerciseId);
            verify(workoutSetRepository).findMaxRepsByUserAndExercise(userId, exerciseId);
            verify(workoutSetRepository).findDateOfMaxWeight(userId, exerciseId, maxWeight);
        }

        @Test
        @DisplayName("Deve ignorar a busca por data se a carga máxima for zero (Ex: Exercício Calistênico / Peso Corporal)")
        void getPersonalRecord_WithZeroWeight_ShouldNotQueryDate() {
            // Arrange
            BigDecimal maxWeight = BigDecimal.ZERO;
            int maxReps = 25;

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
            when(workoutSetRepository.findMaxWeightByUserAndExercise(userId, exerciseId)).thenReturn(Optional.of(maxWeight));
            when(workoutSetRepository.findMaxRepsByUserAndExercise(userId, exerciseId)).thenReturn(Optional.of(maxReps));

            // Act
            PersonalRecordResponse response = personalRecordService.getPersonalRecord(userId, exerciseId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.maxWeightKg()).isEqualTo(BigDecimal.ZERO);
            assertThat(response.maxReps()).isEqualTo(25);
            assertThat(response.achievedAt()).isNull(); // Deve permanecer nulo

            // Garante que a busca por data não foi executada por causa do IF da regra de negócio
            verify(workoutSetRepository, never()).findDateOfMaxWeight(anyLong(), anyLong(), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Deve retornar valores zerados e data nula se o usuário nunca executou o exercício")
        void getPersonalRecord_WithNoExecutionHistory_ShouldReturnZeroedResponse() {
            // Arrange
            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
            when(workoutSetRepository.findMaxWeightByUserAndExercise(userId, exerciseId)).thenReturn(Optional.empty());
            when(workoutSetRepository.findMaxRepsByUserAndExercise(userId, exerciseId)).thenReturn(Optional.empty());

            // Act
            PersonalRecordResponse response = personalRecordService.getPersonalRecord(userId, exerciseId);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.maxWeightKg()).isEqualTo(BigDecimal.ZERO); // .orElse(BigDecimal.ZERO)
            assertThat(response.maxReps()).isZero(); // .orElse(0)
            assertThat(response.achievedAt()).isNull();

            verify(workoutSetRepository, never()).findDateOfMaxWeight(anyLong(), anyLong(), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException se o ID do exercício informado não existir")
        void getPersonalRecord_WithInvalidExercise_ShouldThrowException() {
            // Arrange
            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> personalRecordService.getPersonalRecord(userId, exerciseId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Exercício não encontrado");

            // Valida que nenhuma query de histórico foi disparada se o exercício é inválido
            verify(workoutSetRepository, never()).findMaxWeightByUserAndExercise(anyLong(), anyLong());
            verify(workoutSetRepository, never()).findMaxRepsByUserAndExercise(anyLong(), anyLong());
        }
    }
}