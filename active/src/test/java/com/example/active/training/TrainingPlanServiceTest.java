package com.example.active.training;

import com.example.active.dto.*;
import com.example.active.model.Exercise;
import com.example.active.model.SplitFocus;
import com.example.active.model.TrainingGoal;
import com.example.active.model.TrainingPlan;
import com.example.active.repository.ExerciseRepository;
import com.example.active.service.TrainingPlanService;
import com.example.active.repository.TrainingPlanRepository;
import com.example.active.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private TrainingPlanService trainingPlanService;

    private User mockUser;
    private Exercise mockExercise;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockExercise = new Exercise();
        mockExercise.setId(50L);
        mockExercise.setTitle("Supino Reto");
    }

    @Nested
    @DisplayName("Testes do método Create")
    class CreateMethodTests {

        @Test
        @DisplayName("Deve criar plano de treino completo com sucesso quando dados e exercícios forem válidos")
        void create_ShouldReturnTrainingPlanResponse_WhenDataIsValid() {
            // Arrange
            ExerciseSlotRequest slotReq = new ExerciseSlotRequest(
                    50L,
                    4,
                    10,
                    new BigDecimal("60.00"),
                    90
            );

            TrainingPlanDayRequest dayReq = new TrainingPlanDayRequest(
                    DayOfWeek.MONDAY,
                    SplitFocus.UPPER_PUSH,
                    List.of(slotReq)
            );

            TrainingPlanWeekRequest weekReq = new TrainingPlanWeekRequest(1, List.of(dayReq));

            TrainingPlanCreateRequest request = new TrainingPlanCreateRequest(
                    "Foco em Hipertrofia",
                    TrainingGoal.MUSCLE_GAIN,
                    4,
                    List.of(weekReq)
            );

            when(exerciseRepository.findAllById(Set.of(50L))).thenReturn(List.of(mockExercise));

            when(trainingPlanRepository.save(any(TrainingPlan.class))).thenAnswer(invocation -> {
                TrainingPlan planToSave = invocation.getArgument(0);
                planToSave.setId(100L);
                planToSave.setCreatedAt(LocalDateTime.now());

                if (planToSave.getWeeks() != null) {
                    planToSave.getWeeks().forEach(w -> {
                        w.setId(200L);
                        if (w.getDays() != null) {
                            w.getDays().forEach(d -> {
                                d.setId(300L);
                                if (d.getExercises() != null) {
                                    d.getExercises().forEach(s -> s.setId(400L));
                                }
                            });
                        }
                    });
                }
                return planToSave;
            });

            // Act
            TrainingPlanCreateResponse response = trainingPlanService.create(request, mockUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(100L);
            assertThat(response.name()).isEqualTo("Foco em Hipertrofia");
            assertThat(response.goal()).isEqualTo(TrainingGoal.MUSCLE_GAIN);
            assertThat(response.weeks()).hasSize(1);

            TrainingPlanWeekResponse weekRes = response.weeks().get(0);
            assertThat(weekRes.id()).isEqualTo(200L);
            assertThat(weekRes.days()).hasSize(1);

            TrainingPlanDayResponse dayRes = weekRes.days().get(0);
            assertThat(dayRes.id()).isEqualTo(300L);
            assertThat(dayRes.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(dayRes.splitFocus()).isEqualTo(SplitFocus.UPPER_PUSH);
            assertThat(dayRes.exercises()).hasSize(1);

            ExerciseSlotResponse slotRes = dayRes.exercises().get(0);
            assertThat(slotRes.id()).isEqualTo(400L);
            assertThat(slotRes.exerciseId()).isEqualTo(50L);
            assertThat(slotRes.sets()).isEqualTo(4);
            assertThat(slotRes.reps()).isEqualTo(10);
            assertThat(slotRes.weightKg()).isEqualTo(new BigDecimal("60.00"));

            verify(exerciseRepository, times(1)).findAllById(Set.of(50L));
            verify(trainingPlanRepository, times(1)).save(any(TrainingPlan.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando um ou mais exercícios do plano não existirem no banco")
        void create_ShouldThrowRuntimeException_WhenExerciseDoesNotExist() {
            // Arrange
            ExerciseSlotRequest slotReq = new ExerciseSlotRequest(999L, 3, 12, new BigDecimal("20.00"), 60);
            TrainingPlanDayRequest dayReq = new TrainingPlanDayRequest(DayOfWeek.TUESDAY, SplitFocus.UPPER_PULL, List.of(slotReq));
            TrainingPlanWeekRequest weekReq = new TrainingPlanWeekRequest(1, List.of(dayReq));
            TrainingPlanCreateRequest request = new TrainingPlanCreateRequest("Plano Inválido", TrainingGoal.MUSCLE_GAIN, 4, List.of(weekReq));

            when(exerciseRepository.findAllById(Set.of(999L))).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> trainingPlanService.create(request, mockUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Um ou mais exercícios informados não existem");

            verify(trainingPlanRepository, never()).save(any(TrainingPlan.class));
        }
    }

    @Nested
    @DisplayName("Testes do método List")
    class ListMethodTests {

        @Test
        @DisplayName("Deve retornar uma página de TrainingPlanCreateResponse filtrando pelo usuário logado")
        void list_ShouldReturnPagedTrainingPlans_ForLoggedUser() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<TrainingPlan> inputSpec = mock(Specification.class);

            TrainingPlan plan = TrainingPlan.builder()
                    .id(100L)
                    .name("Plano do User")
                    .goal(TrainingGoal.MUSCLE_GAIN)
                    .weekCount(4)
                    .weeks(Collections.emptyList())
                    .build();

            Page<TrainingPlan> page = new PageImpl<>(List.of(plan), pageable, 1);

            when(trainingPlanRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // Act
            Page<TrainingPlanCreateResponse> result = trainingPlanService.list(mockUser, inputSpec, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(100L);
            assertThat(result.getContent().get(0).goal()).isEqualTo(TrainingGoal.MUSCLE_GAIN);

            verify(trainingPlanRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("Testes do método Detail")
    class DetailMethodTests {

        @Test
        @DisplayName("Deve retornar os detalhes do plano quando o ID existir e pertencer ao usuário")
        void detail_ShouldReturnTrainingPlanDetails_WhenIdAndUserMatch() {
            // Arrange
            Long planId = 100L;
            TrainingPlan plan = TrainingPlan.builder()
                    .id(planId)
                    .name("Plano de Força")
                    .goal(TrainingGoal.CONDITIONING)
                    .weekCount(6)
                    .weeks(Collections.emptyList())
                    .build();

            when(trainingPlanRepository.findByIdAndUserIdWithDetails(planId, mockUser.getId()))
                    .thenReturn(Optional.of(plan));

            // Act
            TrainingPlanCreateResponse response = trainingPlanService.detail(planId, mockUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(planId);
            assertThat(response.name()).isEqualTo("Plano de Força");
            assertThat(response.goal()).isEqualTo(TrainingGoal.CONDITIONING);

            verify(trainingPlanRepository, times(1)).findByIdAndUserIdWithDetails(planId, mockUser.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção quando o plano não for encontrado ou pertencer a outro usuário")
        void detail_ShouldThrowException_WhenPlanNotFoundOrAccessDenied() {
            // Arrange
            Long planId = 999L;
            when(trainingPlanRepository.findByIdAndUserIdWithDetails(planId, mockUser.getId()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> trainingPlanService.detail(planId, mockUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Plano não encontrado ou acesso negado");
        }
    }
}