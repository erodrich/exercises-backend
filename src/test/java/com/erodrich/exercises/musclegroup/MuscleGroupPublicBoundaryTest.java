package com.erodrich.exercises.musclegroup;

import com.erodrich.exercises.musclegroup.dto.MuscleGroupDTO;
import com.erodrich.exercises.musclegroup.service.MuscleGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuscleGroupPublicBoundaryTest {

    @Mock
    private MuscleGroupService muscleGroupService;

    @InjectMocks
    private MuscleGroupPublicBoundary muscleGroupPublicBoundary;

    private MuscleGroupDTO chestGroup;
    private MuscleGroupDTO backGroup;

    @BeforeEach
    void setUp() {
        chestGroup = new MuscleGroupDTO(1L, "CHEST", "Chest exercises");
        backGroup = new MuscleGroupDTO(2L, "BACK", "Back exercises");
    }

    @Test
    void getAllMuscleGroups_ShouldReturnListOfMuscleGroups() {
        // Given
        List<MuscleGroupDTO> muscleGroups = Arrays.asList(chestGroup, backGroup);
        when(muscleGroupService.getAllMuscleGroups()).thenReturn(muscleGroups);

        // When
        ResponseEntity<List<MuscleGroupDTO>> response = muscleGroupPublicBoundary.getAllMuscleGroups();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(chestGroup, backGroup);
    }

    @Test
    void getAllMuscleGroups_WhenEmpty_ShouldReturnEmptyList() {
        // Given
        when(muscleGroupService.getAllMuscleGroups()).thenReturn(List.of());

        // When
        ResponseEntity<List<MuscleGroupDTO>> response = muscleGroupPublicBoundary.getAllMuscleGroups();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getMuscleGroupById_WhenExists_ShouldReturnMuscleGroup() {
        // Given
        Long id = 1L;
        when(muscleGroupService.getMuscleGroupById(id)).thenReturn(Optional.of(chestGroup));

        // When
        ResponseEntity<MuscleGroupDTO> response = muscleGroupPublicBoundary.getMuscleGroupById(id);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("CHEST");
    }

    @Test
    void getMuscleGroupById_WhenNotExists_ShouldReturn404() {
        // Given
        Long id = 999L;
        when(muscleGroupService.getMuscleGroupById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MuscleGroupDTO> response = muscleGroupPublicBoundary.getMuscleGroupById(id);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getMuscleGroupByName_WhenExists_ShouldReturnMuscleGroup() {
        // Given
        String name = "CHEST";
        when(muscleGroupService.getMuscleGroupByName(name)).thenReturn(Optional.of(chestGroup));

        // When
        ResponseEntity<MuscleGroupDTO> response = muscleGroupPublicBoundary.getMuscleGroupByName(name);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("CHEST");
    }

    @Test
    void getMuscleGroupByName_WhenNotExists_ShouldReturn404() {
        // Given
        String name = "NONEXISTENT";
        when(muscleGroupService.getMuscleGroupByName(name)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MuscleGroupDTO> response = muscleGroupPublicBoundary.getMuscleGroupByName(name);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getMuscleGroupByName_WhenCaseInsensitive_ShouldReturnMuscleGroup() {
        // Given
        String name = "chest";
        when(muscleGroupService.getMuscleGroupByName(name)).thenReturn(Optional.of(chestGroup));

        // When
        ResponseEntity<MuscleGroupDTO> response = muscleGroupPublicBoundary.getMuscleGroupByName(name);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("CHEST");
    }
}
