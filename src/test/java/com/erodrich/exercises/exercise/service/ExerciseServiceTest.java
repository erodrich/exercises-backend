package com.erodrich.exercises.exercise.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.mapper.ExerciseMapper;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {
	
	@Mock
	private ExerciseRepository exerciseRepository;
	
	@Mock
	private ExerciseMapper exerciseMapper;
	
	@Mock
	private MuscleGroupRepository muscleGroupRepository;
	
	@InjectMocks
	private ExerciseService exerciseService;
	
	@Test
	void getAllExercises_shouldReturnListOfExercises() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(1L, "CHEST", "Chest exercises");
		MuscleGroupEntity legs = new MuscleGroupEntity(4L, "LEGS", "Leg exercises");
		
		ExerciseEntity entity1 = new ExerciseEntity();
		entity1.setId(1L);
		entity1.setName("Bench Press");
		entity1.setMuscleGroup(chest);
		
		ExerciseEntity entity2 = new ExerciseEntity();
		entity2.setId(2L);
		entity2.setName("Squat");
		entity2.setMuscleGroup(legs);
		
		ExerciseDTO dto1 = new ExerciseDTO(1L, "Bench Press", "CHEST");
		ExerciseDTO dto2 = new ExerciseDTO(2L, "Squat", "LEGS");
		
		when(exerciseRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
		when(exerciseMapper.toDTO(entity1)).thenReturn(dto1);
		when(exerciseMapper.toDTO(entity2)).thenReturn(dto2);
		
		// When
		List<ExerciseDTO> result = exerciseService.getAllExercises();
		
		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("Bench Press");
		assertThat(result.get(1).getName()).isEqualTo("Squat");
	}
	
	@Test
	void getExerciseById_whenExists_shouldReturnExercise() {
		// Given
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		
		ExerciseEntity entity = new ExerciseEntity();
		entity.setId(1L);
		entity.setName("Deadlift");
		entity.setMuscleGroup(back);
		
		ExerciseDTO dto = new ExerciseDTO(1L, "Deadlift", "BACK");
		
		when(exerciseRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(exerciseMapper.toDTO(entity)).thenReturn(dto);
		
		// When
		ExerciseDTO result = exerciseService.getExerciseById(1L);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Deadlift");
	}
	
	@Test
	void getExerciseById_whenNotExists_shouldThrowException() {
		// Given
		when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> exerciseService.getExerciseById(999L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Exercise not found");
	}
	
	@Test
	void createExercise_withValidData_shouldReturnCreatedExercise() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(null, "Pull Up", "BACK");
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		
		ExerciseEntity entity = new ExerciseEntity();
		entity.setName("Pull Up");
		entity.setMuscleGroup(back);
		
		ExerciseEntity savedEntity = new ExerciseEntity();
		savedEntity.setId(1L);
		savedEntity.setName("Pull Up");
		savedEntity.setMuscleGroup(back);
		
		ExerciseDTO savedDTO = new ExerciseDTO(1L, "Pull Up", "BACK");
		
		when(muscleGroupRepository.findByNameIgnoreCase("BACK")).thenReturn(Optional.of(back));
		when(exerciseRepository.findByNameAndMuscleGroup("Pull Up", back)).thenReturn(Optional.empty());
		when(exerciseMapper.toEntity(dto)).thenReturn(entity);
		when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(savedEntity);
		when(exerciseMapper.toDTO(savedEntity)).thenReturn(savedDTO);
		
		// When
		ExerciseDTO result = exerciseService.createExercise(dto);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("Pull Up");
	}
	
	@Test
	void createExercise_withInvalidMuscleGroup_shouldThrowException() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(null, "Test", "INVALID");
		
		when(muscleGroupRepository.findByNameIgnoreCase("INVALID")).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> exerciseService.createExercise(dto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Invalid muscle group");
	}
	
	@Test
	void createExercise_withDuplicateExercise_shouldThrowException() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(null, "Bench Press", "CHEST");
		MuscleGroupEntity chest = new MuscleGroupEntity(1L, "CHEST", "Chest exercises");
		
		ExerciseEntity existingEntity = new ExerciseEntity();
		existingEntity.setName("Bench Press");
		existingEntity.setMuscleGroup(chest);
		
		when(muscleGroupRepository.findByNameIgnoreCase("CHEST")).thenReturn(Optional.of(chest));
		when(exerciseRepository.findByNameAndMuscleGroup("Bench Press", chest))
			.thenReturn(Optional.of(existingEntity));
		
		// When/Then
		assertThatThrownBy(() -> exerciseService.createExercise(dto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Exercise already exists");
	}
	
	@Test
	void updateExercise_whenExists_shouldReturnUpdatedExercise() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(1L, "CHEST", "Chest exercises");
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		
		ExerciseEntity existing = new ExerciseEntity();
		existing.setId(1L);
		existing.setName("Old Name");
		existing.setMuscleGroup(chest);
		
		ExerciseDTO dto = new ExerciseDTO(1L, "New Name", "BACK");
		
		ExerciseEntity updatedEntity = new ExerciseEntity();
		updatedEntity.setId(1L);
		updatedEntity.setName("New Name");
		updatedEntity.setMuscleGroup(back);
		
		ExerciseDTO updatedDTO = new ExerciseDTO(1L, "New Name", "BACK");
		
		when(exerciseRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(muscleGroupRepository.findByNameIgnoreCase("BACK")).thenReturn(Optional.of(back));
		when(exerciseRepository.save(existing)).thenReturn(updatedEntity);
		when(exerciseMapper.toDTO(updatedEntity)).thenReturn(updatedDTO);
		
		// When
		ExerciseDTO result = exerciseService.updateExercise(1L, dto);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("New Name");
		assertThat(result.getGroup()).isEqualTo("BACK");
	}
	
	@Test
	void updateExercise_whenNotExists_shouldThrowException() {
		// Given
		ExerciseDTO dto = new ExerciseDTO(1L, "Test", "CHEST");
		
		when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> exerciseService.updateExercise(1L, dto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Exercise not found");
	}
	
	@Test
	void deleteExercise_whenExists_shouldDelete() {
		// Given
		when(exerciseRepository.existsById(1L)).thenReturn(true);
		
		// When
		exerciseService.deleteExercise(1L);
		
		// Then
		verify(exerciseRepository).deleteById(1L);
	}
	
	@Test
	void deleteExercise_whenNotExists_shouldThrowException() {
		// Given
		when(exerciseRepository.existsById(999L)).thenReturn(false);
		
		// When/Then
		assertThatThrownBy(() -> exerciseService.deleteExercise(999L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Exercise not found");
	}
}
