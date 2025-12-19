package com.erodrich.exercises.exercise.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseMapperTest {
	
	private ExerciseMapper exerciseMapper;
	
	@Mock
	private MuscleGroupRepository muscleGroupRepository;
	
	@BeforeEach
	void setUp() {
		exerciseMapper = new ExerciseMapper(muscleGroupRepository);
		
		// Setup mock muscle groups
		MuscleGroupEntity chest = new MuscleGroupEntity(1L, "CHEST", "Chest exercises");
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		MuscleGroupEntity shoulders = new MuscleGroupEntity(3L, "SHOULDERS", "Shoulder exercises");
		MuscleGroupEntity legs = new MuscleGroupEntity(4L, "LEGS", "Leg exercises");
		MuscleGroupEntity biceps = new MuscleGroupEntity(5L, "BICEPS", "Bicep exercises");
		MuscleGroupEntity triceps = new MuscleGroupEntity(6L, "TRICEPS", "Tricep exercises");
		
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("CHEST")).thenReturn(Optional.of(chest));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("chest")).thenReturn(Optional.of(chest));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("BACK")).thenReturn(Optional.of(back));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("back")).thenReturn(Optional.of(back));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("SHOULDERS")).thenReturn(Optional.of(shoulders));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("shoulders")).thenReturn(Optional.of(shoulders));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("LEGS")).thenReturn(Optional.of(legs));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("legs")).thenReturn(Optional.of(legs));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("BICEPS")).thenReturn(Optional.of(biceps));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("biceps")).thenReturn(Optional.of(biceps));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("TRICEPS")).thenReturn(Optional.of(triceps));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("triceps")).thenReturn(Optional.of(triceps));
	}
	
	@Test
	void toEntity_withValidDTO_shouldMapCorrectly() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(1L, "Bench Press", "CHEST");
		
		// When
		ExerciseEntity entity = exerciseMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1L);
		assertThat(entity.getName()).isEqualTo("Bench Press");
		assertThat(entity.getMuscleGroup().getName()).isEqualTo("CHEST");
	}
	
	@Test
	void toEntity_withLowercaseGroup_shouldConvertToUppercase() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(1L, "Squat", "legs");
		
		// When
		ExerciseEntity entity = exerciseMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getMuscleGroup().getName()).isEqualTo("LEGS");
	}
	
	@Test
	void toEntity_withNullDTO_shouldReturnNull() {
		// When
		ExerciseEntity entity = exerciseMapper.toEntity(null);
		
		// Then
		assertThat(entity).isNull();
	}
	
	@Test
	void toDTO_withValidEntity_shouldMapCorrectly() {
		// Given
		MuscleGroupEntity muscleGroup = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		ExerciseEntity entity = new ExerciseEntity();
		entity.setId(1L);
		entity.setName("Deadlift");
		entity.setMuscleGroup(muscleGroup);
		
		// When
		ExerciseDTO dto = exerciseMapper.toDTO(entity);
		
		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getName()).isEqualTo("Deadlift");
		assertThat(dto.getGroup()).isEqualTo("BACK");
	}
	
	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		ExerciseDTO dto = exerciseMapper.toDTO(null);
		
		// Then
		assertThat(dto).isNull();
	}
	
	@Test
	void toEntity_withAllMuscleGroups_shouldMapCorrectly() {
		// Test all muscle groups
		String[] groups = {"CHEST", "BACK", "SHOULDERS", "LEGS", "BICEPS", "TRICEPS"};
		for (String groupName : groups) {
			// Given
			ExerciseDTO dto = new ExerciseDTO(null, "Test Exercise", groupName);
			
			// When
			ExerciseEntity entity = exerciseMapper.toEntity(dto);
			
			// Then
			assertThat(entity.getMuscleGroup().getName()).isEqualTo(groupName);
		}
	}
}
