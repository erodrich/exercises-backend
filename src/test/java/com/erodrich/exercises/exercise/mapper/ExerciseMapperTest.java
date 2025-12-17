package com.erodrich.exercises.exercise.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;

class ExerciseMapperTest {
	
	private ExerciseMapper exerciseMapper;
	
	@BeforeEach
	void setUp() {
		exerciseMapper = new ExerciseMapper();
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
		assertThat(entity.getGroup()).isEqualTo(MuscleGroup.CHEST);
	}
	
	@Test
	void toEntity_withLowercaseGroup_shouldConvertToUppercase() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(1L, "Squat", "legs");
		
		// When
		ExerciseEntity entity = exerciseMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getGroup()).isEqualTo(MuscleGroup.LEGS);
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
		ExerciseEntity entity = new ExerciseEntity();
		entity.setId(1L);
		entity.setName("Deadlift");
		entity.setGroup(MuscleGroup.BACK);
		
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
		for (MuscleGroup group : MuscleGroup.values()) {
			// Given
			ExerciseDTO dto = new ExerciseDTO(null, "Test Exercise", group.name());
			
			// When
			ExerciseEntity entity = exerciseMapper.toEntity(dto);
			
			// Then
			assertThat(entity.getGroup()).isEqualTo(group);
		}
	}
}
