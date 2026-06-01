package com.example.active.workout;

import com.example.active.model.Exercise;
import com.example.active.repository.ExerciseRepository;
import com.example.active.service.WorkoutSessionService;
import com.example.active.model.TrainingPlanDay;
import com.example.active.repository.TrainingPlanDayRepository;
import com.example.active.model.User;
import com.example.active.dto.WorkoutSessionCreateRequest;
import com.example.active.dto.WorkoutSessionCreateResponse;
import com.example.active.model.WorkoutSession;
import com.example.active.repository.WorkoutSessionRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class WorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private TrainingPlanDayRepository trainingPlanDayRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private WorkoutSessionService workoutSessionService;

    private User mockUser;
    private Exercise mockExercise;
    private TrainingPlanDay mockTrainingPlanDay;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockExercise = new Exercise();
        mockExercise.setId(50L);
        mockExercise.setTitle("Agachamento Livre");

        mockTrainingPlanDay = new TrainingPlanDay();
        mockTrainingPlanDay.setId(10L);
    }

    @Nested
    @DisplayName("Testes do método registerSession")
    class RegisterSessionTests {

        @Test
        @DisplayName("Deve registrar treino vinculado a um plano de treino com sucesso")
        void registerSession_WithTrainingPlanDay_ShouldSuccess() {
            // Arrange
            var setReq = new WorkoutSessionCreateRequest.SessionSetRequest(10, new BigDecimal("80.00"));
            var exReq = new WorkoutSessionCreateRequest.SessionExerciseRequest(50L, List.of(setReq));
            var request = new WorkoutSessionCreateRequest.Create(10L, LocalDate.now(), List.of(exReq));

            when(trainingPlanDayRepository.findById(10L)).thenReturn(Optional.of(mockTrainingPlanDay));
            when(exerciseRepository.findAllById(Set.of(50L))).thenReturn(List.of(mockExercise));

            when(workoutSessionRepository.save(any(WorkoutSession.class))).thenAnswer(invocation -> {
                WorkoutSession session = invocation.getArgument(0);
                session.setId(200L);
                session.setCreatedAt(LocalDateTime.now());

                if (session.getExercises() != null) {
                    session.getExercises().forEach(we -> {
                        we.setId(300L);
                        if (we.getSets() != null) {
                            we.getSets().forEach(s -> s.setId(400L));
                        }
                    });
                }
                return session;
            });

            // Act
            WorkoutSessionCreateResponse.WorkoutSessionResponse response = workoutSessionService.registerSession(request, mockUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(200L);
            assertThat(response.trainingPlanDayId()).isEqualTo(10L);
            assertThat(response.totalExercises()).isEqualTo(1);

            var exRes = response.exercises().get(0);
            assertThat(exRes.exerciseId()).isEqualTo(50L);
            assertThat(exRes.exerciseTitle()).isEqualTo("Agachamento Livre");
            assertThat(exRes.sets()).hasSize(1);
            assertThat(exRes.sets().get(0).reps()).isEqualTo(10);
            assertThat(exRes.sets().get(0).weightKg()).isEqualTo(new BigDecimal("80.00"));

            verify(trainingPlanDayRepository).findById(10L);
            verify(workoutSessionRepository).save(any(WorkoutSession.class));
        }

        @Test
        @DisplayName("Deve registrar treino avulso com sucesso (quando trainingPlanDayId é nulo)")
        void registerSession_WithoutTrainingPlanDay_ShouldSuccess() {
            // Arrange
            var setReq = new WorkoutSessionCreateRequest.SessionSetRequest(12, new BigDecimal("40.00"));
            var exReq = new WorkoutSessionCreateRequest.SessionExerciseRequest(50L, List.of(setReq));
            var request = new WorkoutSessionCreateRequest.Create(null, LocalDate.now(), List.of(exReq));

            when(exerciseRepository.findAllById(Set.of(50L))).thenReturn(List.of(mockExercise));
            when(workoutSessionRepository.save(any(WorkoutSession.class))).thenAnswer(invocation -> {
                WorkoutSession session = invocation.getArgument(0);
                session.setId(201L);
                return session;
            });

            // Act
            WorkoutSessionCreateResponse.WorkoutSessionResponse response = workoutSessionService.registerSession(request, mockUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(201L);
            assertThat(response.trainingPlanDayId()).isNull();

            verify(trainingPlanDayRepository, never()).findById(anyLong());
            verify(workoutSessionRepository).save(any(WorkoutSession.class));
        }

        @Test
        @DisplayName("Deve lançar erro 404 se o dia do plano de treino informado não existir")
        void registerSession_ShouldThrowNotFound_WhenTrainingPlanDayDoesNotExist() {
            var request = new WorkoutSessionCreateRequest.Create(999L, LocalDate.now(), List.of());

            when(trainingPlanDayRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutSessionService.registerSession(request, mockUser))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Dia do plano não encontrado");

            verify(workoutSessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar erro 404 se um ID de exercício enviado não existir no banco")
        void registerSession_ShouldThrowNotFound_WhenExerciseIsInvalid() {
            var setReq = new WorkoutSessionCreateRequest.SessionSetRequest(10, new BigDecimal("10.00"));
            var exReq = new WorkoutSessionCreateRequest.SessionExerciseRequest(999L, List.of(setReq));
            var request = new WorkoutSessionCreateRequest.Create(null, LocalDate.now(), List.of(exReq));

            when(exerciseRepository.findAllById(Set.of(999L))).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> workoutSessionService.registerSession(request, mockUser))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Exercício inválido");

            verify(workoutSessionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método detail")
    class DetailMethodTests {

        @Test
        @DisplayName("Deve retornar os detalhes do treino se pertencer ao usuário logado")
        void detail_ShouldReturnSession_WhenIdAndUserMatch() {
            var session = WorkoutSession.builder()
                    .id(200L)
                    .date(LocalDate.now())
                    .user(mockUser)
                    .exercises(new ArrayList<>())
                    .build();

            when(workoutSessionRepository.findByIdAndUserIdWithDetails(200L, mockUser.getId()))
                    .thenReturn(Optional.of(session));

            var response = workoutSessionService.detail(200L, mockUser);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Deve lançar erro 404 ao tentar detalhar treino de outro usuário ou inexistente")
        void detail_ShouldThrowNotFound_WhenNotFoundOrAccessDenied() {
            when(workoutSessionRepository.findByIdAndUserIdWithDetails(999L, mockUser.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutSessionService.detail(999L, mockUser))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Treino não encontrado ou acesso negado");
        }
    }

    @Nested
    @DisplayName("Testes do método list")
    class ListMethodTests {

        @Test
        @DisplayName("Deve retornar página de treinos com sucesso se existirem dados")
        void list_ShouldReturnPagedSessions_WhenDataExists() {
            Pageable pageable = PageRequest.of(0, 10);
            Specification<WorkoutSession> inputSpec = mock(Specification.class);

            var session = WorkoutSession.builder()
                    .id(200L)
                    .date(LocalDate.now())
                    .exercises(Collections.emptyList())
                    .build();

            Page<WorkoutSession> page = new PageImpl<>(List.of(session), pageable, 1);

            when(workoutSessionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            var result = workoutSessionService.list(mockUser, inputSpec, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException se a listagem de treinos do usuário vier vazia")
        void list_ShouldThrowEntityNotFoundException_WhenPageIsEmpty() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<WorkoutSession> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(workoutSessionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            assertThatThrownBy(() -> workoutSessionService.list(mockUser, null, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Nenhuma sessão de treino encontrada.");
        }
    }
}