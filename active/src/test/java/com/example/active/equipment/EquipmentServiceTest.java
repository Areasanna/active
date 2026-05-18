package com.example.active.equipment;

import com.example.active.equipment.dto.EquipmentRequest;
import com.example.active.equipment.dto.EquipmentResponse;
import com.example.active.equipment.model.Equipment;
import com.example.active.equipment.repository.EquipmentRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment equipment;

    @BeforeEach
    void setUp() {
        // Criação de um mock/objeto de sustentação usando o padrão Builder
        equipment = Equipment.builder()
                .id(1L)
                .name("Halter")
                .build();
    }

    @Nested
    @DisplayName("Testes do método List")
    class ListMethodTests {

        @Test
        @DisplayName("Deve retornar uma página de EquipmentResponse quando existirem dados")
        void list_ShouldReturnPagedEquipmentResponse_WhenDataExists() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Equipment> spec = mock(Specification.class);
            Page<Equipment> page = new PageImpl<>(List.of(equipment), pageable, 1);

            when(equipmentRepository.findAll(spec, pageable)).thenReturn(page);

            // Act
            Page<EquipmentResponse> result = equipmentService.list(spec, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(1L);
            assertThat(result.getContent().get(0).name()).isEqualTo("Halter");

            verify(equipmentRepository, times(1)).findAll(spec, pageable);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a página retornada for vazia")
        void list_ShouldThrowEntityNotFoundException_WhenPageIsEmpty() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Specification<Equipment> spec = mock(Specification.class);
            Page<Equipment> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(equipmentRepository.findAll(spec, pageable)).thenReturn(emptyPage);

            // Act & Assert
            assertThatThrownBy(() -> equipmentService.list(spec, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Nenhum equipamento encontrado para os parâmetros fornecidos.");

            verify(equipmentRepository, times(1)).findAll(spec, pageable);
        }
    }

    @Nested
    @DisplayName("Testes do método Create")
    class CreateMethodTests {

        @Test
        @DisplayName("Deve criar um equipamento com sucesso quando o nome não estiver duplicado")
        void create_ShouldReturnEquipmentResponse_WhenNameIsUnique() {
            // Arrange
            EquipmentRequest request = new EquipmentRequest("Halter");

            when(equipmentRepository.existsByName(request.name())).thenReturn(false);
            when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

            // Act
            EquipmentResponse response = equipmentService.create(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Halter");

            verify(equipmentRepository, times(1)).existsByName("Halter");
            verify(equipmentRepository, times(1)).save(any(Equipment.class));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (400) quando o equipamento já estiver cadastrado")
        void create_ShouldThrowBadRequestException_WhenEquipmentAlreadyExists() {
            // Arrange
            EquipmentRequest request = new EquipmentRequest("Halter");

            when(equipmentRepository.existsByName(request.name())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> equipmentService.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("Equipamento já cadastrado");

            // Garante que o metodo save NUNCA foi chamado por conta do erro prévio
            verify(equipmentRepository, never()).save(any(Equipment.class));
        }
    }
}