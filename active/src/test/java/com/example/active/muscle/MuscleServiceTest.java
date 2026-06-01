package com.example.active.muscle;

import com.example.active.dto.MuscleRequest;
import com.example.active.dto.MuscleResponse;
import com.example.active.model.Muscle;
import com.example.active.repository.MuscleRepository;
import com.example.active.service.MuscleService;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuscleServiceTest {

    @Mock
    private MuscleRepository repository;

    @InjectMocks
    private MuscleService muscleService;

    private Muscle muscle;

    @BeforeEach
    void setUp() {
        muscle = Muscle.builder()
                .id(10L)
                .name("Peitoral Maior")
                .nameEn("Pectoralis Major")
                .build();
    }

    @Nested
    @DisplayName("Testes do método List")
    class ListMethodTests {

        @Test
        @DisplayName("Deve retornar uma página de MuscleResponse quando existirem dados")
        void list_ShouldReturnPagedMuscleResponse_WhenDataExists() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Muscle> spec = mock(Specification.class);
            Page<Muscle> page = new PageImpl<>(List.of(muscle), pageable, 1);

            when(repository.findAll(spec, pageable)).thenReturn(page);

            // Act
            Page<MuscleResponse> result = muscleService.list(spec, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            MuscleResponse response = result.getContent().get(0);
            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.name()).isEqualTo("Peitoral Maior");
            assertThat(response.nameEn()).isEqualTo("Pectoralis Major");

            verify(repository, times(1)).findAll(spec, pageable);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a página retornada for vazia")
        void list_ShouldThrowEntityNotFoundException_WhenPageIsEmpty() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Muscle> spec = mock(Specification.class);
            Page<Muscle> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(repository.findAll(spec, pageable)).thenReturn(emptyPage);

            // Act & Assert
            assertThatThrownBy(() -> muscleService.list(spec, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Nenhum músculo encontrado.");

            verify(repository, times(1)).findAll(spec, pageable);
        }
    }

    @Nested
    @DisplayName("Testes do método Create")
    class CreateMethodTests {

        @Test
        @DisplayName("Deve criar um músculo com sucesso quando os dados forem válidos")
        void create_ShouldReturnMuscleResponse_WhenDataIsValid() {
            // Arrange
            MuscleRequest request = new MuscleRequest("Peitoral Maior", "Pectoralis Major");

            // Em vez de retornar um objeto estático, intercepta o objeto criado pelo builder
            // dentro do Service, injeta o ID simulado e o retorna.
            when(repository.save(any(Muscle.class))).thenAnswer(invocation -> {
                Muscle passedMuscle = invocation.getArgument(0);
                passedMuscle.setId(10L);
                return passedMuscle;
            });

            // Act
            MuscleResponse response = muscleService.create(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.name()).isEqualTo("Peitoral Maior");
            assertThat(response.nameEn()).isEqualTo("Pectoralis Major");

            verify(repository, times(1)).save(any(Muscle.class));
        }
    }
}