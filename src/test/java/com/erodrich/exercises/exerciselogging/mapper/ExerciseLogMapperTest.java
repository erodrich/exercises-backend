package com.erodrich.exercises.exerciselogging.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;
import com.erodrich.exercises.exerciselogging.dto.ExerciseDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseSetDTO;
import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;
import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;

@ExtendWith(MockitoExtension.class)
class ExerciseLogMapperTest {
	
	private ExerciseLogMapper exerciseLogMapper;
	
	@Mock
	private MuscleGroupRepository muscleGroupRepository;
	
	@BeforeEach
	void setUp() {
		exerciseLogMapper = new ExerciseLogMapper(muscleGroupRepository);
		
		// Setup mock muscle groups
		MuscleGroupEntity chest = new MuscleGroupEntity(1L, "CHEST", "Chest exercises");
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		MuscleGroupEntity legs = new MuscleGroupEntity(4L, "LEGS", "Leg exercises");
		
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("CHEST")).thenReturn(Optional.of(chest));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("BACK")).thenReturn(Optional.of(back));
		lenient().when(muscleGroupRepository.findByNameIgnoreCase("LEGS")).thenReturn(Optional.of(legs));
	}
	
	@Test
	void toEntity_withValidDTO_shouldMapCorrectly() {
		// Given
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Bench Press", "CHEST");
		ExerciseSetDTO set1 = new ExerciseSetDTO(100.0, 10);
		ExerciseSetDTO set2 = new ExerciseSetDTO(100.0, 8);
		List<ExerciseSetDTO> sets = Arrays.asList(set1, set2);
		
		ExerciseLogDTO dto = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, sets, false);
		
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getDate()).isEqualTo(LocalDateTime.of(2025, 12, 16, 10, 30, 0));
		assertThat(entity.isHasFailed()).isFalse();
		assertThat(entity.getExercise()).isNotNull();
		assertThat(entity.getExercise().getName()).isEqualTo("Bench Press");
		assertThat(entity.getExercise().getMuscleGroup().getName()).isEqualTo("CHEST");
		assertThat(entity.getSets()).hasSize(2);
	}
	
	@Test
	void toEntity_withNullTimestamp_shouldUseCurrentTime() {
		// Given
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Squat", "LEGS");
		ExerciseLogDTO dto = new ExerciseLogDTO(null, exerciseDTO, Arrays.asList(), false);
		
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getDate()).isNotNull();
		assertThat(entity.getDate()).isBeforeOrEqualTo(LocalDateTime.now());
	}
	
	@Test
	void toEntity_withNullFailure_shouldDefaultToFalse() {
		// Given
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Deadlift", "BACK");
		ExerciseLogDTO dto = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, Arrays.asList(), null);
		
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.isHasFailed()).isFalse();
	}
	
	@Test
	void toEntity_withNullDTO_shouldReturnNull() {
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(null);
		
		// Then
		assertThat(entity).isNull();
	}
	
	@Test
	void toDTO_withValidEntity_shouldMapCorrectly() {
		// Given
		MuscleGroupEntity back = new MuscleGroupEntity(2L, "BACK", "Back exercises");
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setId(1L);
		exercise.setName("Pull Up");
		exercise.setMuscleGroup(back);
		
		ExerciseSetEntity set1 = new ExerciseSetEntity();
		set1.setId(1L);
		set1.setWeight(0.0);
		set1.setReps(10);
		
		ExerciseSetEntity set2 = new ExerciseSetEntity();
		set2.setId(2L);
		set2.setWeight(0.0);
		set2.setReps(8);
		
		Set<ExerciseSetEntity> sets = new HashSet<>(Arrays.asList(set1, set2));
		
		ExerciseLogEntity entity = new ExerciseLogEntity();
		entity.setId(1L);
		entity.setDate(LocalDateTime.of(2025, 12, 16, 10, 30, 0));
		entity.setHasFailed(true);
		entity.setExercise(exercise);
		entity.setSets(sets);
		
		// When
		ExerciseLogDTO dto = exerciseLogMapper.toDTO(entity);
		
		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getTimestamp()).isEqualTo("12/16/2025 10:30:00");
		assertThat(dto.getFailure()).isTrue();
		assertThat(dto.getExercise()).isNotNull();
		assertThat(dto.getExercise().getName()).isEqualTo("Pull Up");
		assertThat(dto.getExercise().getGroup()).isEqualTo("BACK");
		assertThat(dto.getSets()).hasSize(2);
	}
	
	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		ExerciseLogDTO dto = exerciseLogMapper.toDTO(null);
		
		// Then
		assertThat(dto).isNull();
	}
	
	@Test
	void toEntity_withEmptySets_shouldCreateEmptySet() {
		// Given
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Push Up", "CHEST");
		ExerciseLogDTO dto = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, Arrays.asList(), false);
		
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getSets()).isEmpty();
	}
	
	@Test
	void toEntity_withNullSets_shouldCreateEmptySet() {
		// Given
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Push Up", "CHEST");
		ExerciseLogDTO dto = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, null, false);
		
		// When
		ExerciseLogEntity entity = exerciseLogMapper.toEntity(dto);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getSets()).isEmpty();
	}
}
