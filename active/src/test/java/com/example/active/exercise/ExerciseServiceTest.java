package com.example.active.exercise;

import com.example.active.model.Equipment;
import com.example.active.repository.EquipmentRepository;
import com.example.active.dto.ExerciseCreateRequest;
import com.example.active.model.ExerciseResponse;
import com.example.active.dto.ExerciseUpdateRequest;
import com.example.active.model.Exercise;
import com.example.active.model.ExerciseCategory;
import com.example.active.repository.ExerciseRepository;
import com.example.active.model.Muscle;
import com.example.active.repository.MuscleRepository;
import com.example.active.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private MuscleRepository muscleRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Equipment equipment;
    private Muscle primaryMuscle;
    private Muscle secondaryMuscle;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Halter");

        primaryMuscle = new Muscle();
        primaryMuscle.setId(10L);
        primaryMuscle.setName("Bíceps");
        primaryMuscle.setNameEn("Biceps");

        secondaryMuscle = new Muscle();
        secondaryMuscle.setId(20L);
        secondaryMuscle.setName("Antebraço");
        secondaryMuscle.setNameEn("Forearm");

        exercise = new Exercise();
        exercise.setId(100L);
        exercise.setTitle("Rosca Direta");
        exercise.setDescription("Exercício para bíceps");
        exercise.setVideoUrl("http://video.com");
        exercise.setCategory(ExerciseCategory.SHOULDERS);
        exercise.setEquipment(Set.of(equipment));
        exercise.setPrimaryMuscles(Set.of(primaryMuscle));
        exercise.setSecondaryMuscles(Set.of(secondaryMuscle));
    }

    @Nested
    @DisplayName("Testes do método Create")
    class CreateMethodTests {

        @Test
        @DisplayName("Deve criar um exercício com sucesso quando os dados forem válidos")
        void create_ShouldReturnExerciseResponse_WhenDataIsValid() {
            // Arrange
            ExerciseCreateRequest request = new ExerciseCreateRequest(
                    "Rosca Direta", "Exercício para bíceps", "http://video.com",
                    ExerciseCategory.SHOULDERS, List.of(1L), List.of(10L), List.of(20L)
            );

            when(equipmentRepository.findAllById(List.of(1L))).thenReturn(List.of(equipment));
            when(muscleRepository.findAllById(List.of(10L))).thenReturn(List.of(primaryMuscle));
            when(muscleRepository.findAllById(List.of(20L))).thenReturn(List.of(secondaryMuscle));
            when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

            // Act
            ExerciseResponse response = exerciseService.create(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(100L);
            assertThat(response.title()).isEqualTo("Rosca Direta");
            assertThat(response.equipment()).hasSize(1);
            assertThat(response.primaryMuscles()).hasSize(1);
            assertThat(response.secondaryMuscles()).hasSize(1);

            verify(exerciseRepository, times(1)).save(any(Exercise.class));
        }

        @Test
        @DisplayName("Deve lançar erro quando um ID de equipamento for inválido")
        void create_ShouldThrowException_WhenEquipmentIdIsInvalid() {
            ExerciseCreateRequest request = new ExerciseCreateRequest(
                    "Rosca Direta", "Desc", "http://video.com",
                    ExerciseCategory.SHOULDERS, List.of(1L, 2L), List.of(10L), null
            );

            // simula que o banco só achou 1 dos 2 equipamentos enviados
            when(equipmentRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(equipment));

            assertThatThrownBy(() -> exerciseService.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("Um ou mais IDs de equipamentos são inválidos");

            verify(exerciseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar erro quando um ID de músculo primário for inválido")
        void create_ShouldThrowException_WhenPrimaryMuscleIdIsInvalid() {
            ExerciseCreateRequest request = new ExerciseCreateRequest(
                    "Rosca Direta", "Desc", "http://video.com",
                    ExerciseCategory.SHOULDERS, List.of(1L), List.of(10L), null
            );

            when(equipmentRepository.findAllById(List.of(1L))).thenReturn(List.of(equipment));
            when(muscleRepository.findAllById(List.of(10L))).thenReturn(Collections.emptyList()); // Nenhum encontrado

            assertThatThrownBy(() -> exerciseService.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("Um ou mais IDs de músculos primários são inválidos");
        }

        @Test
        @DisplayName("Deve lançar erro quando lista de equipamentos ou músculos primários vier vazia")
        void create_ShouldThrowException_WhenRequiredListsAreEmpty() {
            ExerciseCreateRequest request = new ExerciseCreateRequest(
                    "Rosca Direta", "Desc", "http://video.com",
                    ExerciseCategory.SHOULDERS, List.of(), List.of(), null
            );

            when(equipmentRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
            when(muscleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> exerciseService.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("Deve possuir ao menos um equipamento e músculo primário");
        }
    }

    @Nested
    @DisplayName("Testes do método Update")
    class UpdateMethodTests {

        @Test
        @DisplayName("Deve atualizar um exercício com sucesso")
        void update_ShouldUpdateAndReturnResponse_WhenExerciseExistsAndDataIsValid() {
            // Arrange
            Long exerciseId = 100L;
            ExerciseUpdateRequest request = new ExerciseUpdateRequest(
                    "Rosca Alternada", "Nova descrição", "http://novo-video.com",
                    ExerciseCategory.SHOULDERS, List.of(1L), List.of(10L), null
            );

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
            when(equipmentRepository.findAllById(List.of(1L))).thenReturn(List.of(equipment));
            when(muscleRepository.findAllById(List.of(10L))).thenReturn(List.of(primaryMuscle));
            when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ExerciseResponse response = exerciseService.update(exerciseId, request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.title()).isEqualTo("Rosca Alternada");
            assertThat(response.description()).isEqualTo("Nova descrição");
            assertThat(response.secondaryMuscles()).isEmpty(); // Enviado null, deve limpar a lista

            ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
            verify(exerciseRepository).save(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("Rosca Alternada");
        }

        @Test
        @DisplayName("Deve lançar NotFound ao tentar atualizar exercício inexistente")
        void update_ShouldThrowNotFound_WhenExerciseDoesNotExist() {
            Long exerciseId = 999L;
            ExerciseUpdateRequest request = new ExerciseUpdateRequest(
                    "Titulo", "Desc", "video", ExerciseCategory.SHOULDERS, List.of(1L), List.of(10L), null
            );

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exerciseService.update(exerciseId, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Exercício não encontrado");
        }
    }

    @Nested
    @DisplayName("Testes do método Delete")
    class DeleteMethodTests {

        @Test
        @DisplayName("Deve deletar o exercício se ele existir")
        void delete_ShouldDelete_WhenExerciseExists() {
            Long exerciseId = 100L;
            when(exerciseRepository.existsById(exerciseId)).thenReturn(true);

            exerciseService.delete(exerciseId);

            verify(exerciseRepository, times(1)).deleteById(exerciseId);
        }

        @Test
        @DisplayName("Deve lançar NotFound ao tentar deletar exercício inexistente")
        void delete_ShouldThrowNotFound_WhenExerciseDoesNotExist() {
            Long exerciseId = 999L;
            when(exerciseRepository.existsById(exerciseId)).thenReturn(false);

            assertThatThrownBy(() -> exerciseService.delete(exerciseId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Exercício não encontrado");

            verify(exerciseRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Testes do método FindById")
    class FindByIdMethodTests {

        @Test
        @DisplayName("Deve retornar o exercício quando o ID existir")
        void findById_ShouldReturnResponse_WhenIdExists() {
            Long exerciseId = 100L;
            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

            ExerciseResponse response = exerciseService.findById(exerciseId);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(exerciseId);
            assertThat(response.title()).isEqualTo("Rosca Direta");
        }

        @Test
        @DisplayName("Deve lançar NotFound quando o ID não existir")
        void findById_ShouldThrowNotFound_WhenIdDoesNotExist() {
            Long exerciseId = 999L;
            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exerciseService.findById(exerciseId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Exercício não encontrado");
        }
    }

    @Nested
    @DisplayName("Testes do método List (Paginado com Specification)")
    class ListMethodTests {

        @Test
        @DisplayName("Deve retornar uma página de ExerciseResponse")
        void list_ShouldReturnPagedExerciseResponse() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Exercise> spec = mock(Specification.class);
            Page<Exercise> exercisePage = new PageImpl<>(List.of(exercise), pageable, 1);

            when(exerciseRepository.findAll(spec, pageable)).thenReturn(exercisePage);

            // Act
            Page<ExerciseResponse> result = exerciseService.list(spec, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).title()).isEqualTo("Rosca Direta");
            verify(exerciseRepository, times(1)).findAll(spec, pageable);
        }
    }
}