package com.erodrich.exercises.workoutplan.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.mapper.ExerciseMapper;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.workoutplan.WorkoutPlanTestDataBuilder;
import com.erodrich.exercises.workoutplan.dto.ExerciseTargetDTO;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;

@ExtendWith(MockitoExtension.class)
class ExerciseTargetMapperTest {

	@Mock
	private ExerciseMapper exerciseMapper;

	@Mock
	private ExerciseRepository exerciseRepository;

	private ExerciseTargetMapper mapper;

	private MuscleGroupEntity chest;
	private ExerciseEntity benchPress;
	private ExerciseDTO benchPressDTO;

	@BeforeEach
	void setUp() {
		mapper = new ExerciseTargetMapper(exerciseMapper, exerciseRepository);

		chest = WorkoutPlanTestDataBuilder.createMuscleGroup(1L, "CHEST");
		benchPress = WorkoutPlanTestDataBuilder.createExercise(1L, "Bench Press", chest);
		benchPressDTO = WorkoutPlanTestDataBuilder.createExerciseDTO(1L, "Bench Press", "CHEST");
	}

	@Test
	void toDTO_withValidEntity_shouldMapAllFields() {
		// Given
		ExerciseTargetEntity entity = WorkoutPlanTestDataBuilder.createExerciseTarget(
				1L, benchPress, 4, 8, 12);

		when(exerciseMapper.toDTO(benchPress)).thenReturn(benchPressDTO);

		// When
		ExerciseTargetDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getSets()).isEqualTo(4);
		assertThat(dto.getMinReps()).isEqualTo(8);
		assertThat(dto.getMaxReps()).isEqualTo(12);
		assertThat(dto.getExercise()).isNotNull();
		assertThat(dto.getExercise().getName()).isEqualTo("Bench Press");
	}

	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		ExerciseTargetDTO dto = mapper.toDTO(null);

		// Then
		assertThat(dto).isNull();
	}

	@Test
	void toDTO_shouldMapExerciseUsingExerciseMapper() {
		// Given
		ExerciseTargetEntity entity = WorkoutPlanTestDataBuilder.createExerciseTarget(
				1L, benchPress, 3, 6, 10);

		when(exerciseMapper.toDTO(benchPress)).thenReturn(benchPressDTO);

		// When
		ExerciseTargetDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto.getExercise()).isEqualTo(benchPressDTO);
	}

	@Test
	void toDTO_withNullExercise_shouldHandleGracefully() {
		// Given
		ExerciseTargetEntity entity = WorkoutPlanTestDataBuilder.createExerciseTarget(
				1L, null, 3, 6, 10);

		when(exerciseMapper.toDTO(null)).thenReturn(null);

		// When
		ExerciseTargetDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getExercise()).isNull();
	}

	@Test
	void toEntity_withValidDTO_shouldMapAllFields() {
		// Given
		ExerciseTargetDTO dto = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(
				1L, benchPressDTO, 4, 8, 12);

		when(exerciseRepository.findById(1L)).thenReturn(Optional.of(benchPress));

		// When
		ExerciseTargetEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1L);
		assertThat(entity.getSets()).isEqualTo(4);
		assertThat(entity.getMinReps()).isEqualTo(8);
		assertThat(entity.getMaxReps()).isEqualTo(12);
		assertThat(entity.getExercise()).isNotNull();
		assertThat(entity.getExercise().getName()).isEqualTo("Bench Press");
	}

	@Test
	void toEntity_withNullDTO_shouldReturnNull() {
		// When
		ExerciseTargetEntity entity = mapper.toEntity(null);

		// Then
		assertThat(entity).isNull();
	}

	@Test
	void toEntity_withExerciseId_shouldLookupExercise() {
		// Given
		ExerciseTargetDTO dto = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(
				null, benchPressDTO, 3, 6, 10);

		when(exerciseRepository.findById(1L)).thenReturn(Optional.of(benchPress));

		// When
		ExerciseTargetEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity.getExercise()).isEqualTo(benchPress);
	}

	@Test
	void toEntity_withInvalidExerciseId_shouldThrowException() {
		// Given
		ExerciseTargetDTO dto = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(
				null, benchPressDTO, 3, 6, 10);

		when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

		// When/Then
		assertThatThrownBy(() -> mapper.toEntity(dto))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Exercise not found with ID: 1");
	}

	@Test
	void toEntity_withNullExerciseId_shouldNotLookupExercise() {
		// Given
		ExerciseDTO exerciseDTOWithoutId = WorkoutPlanTestDataBuilder.createExerciseDTO(
				null, "Squat", "LEGS");
		ExerciseTargetDTO dto = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(
				null, exerciseDTOWithoutId, 3, 6, 10);

		// When
		ExerciseTargetEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getExercise()).isNull();
	}

	@Test
	void bidirectionalMapping_shouldPreserveData() {
		// Given
		ExerciseTargetEntity originalEntity = WorkoutPlanTestDataBuilder.createExerciseTarget(
				1L, benchPress, 4, 8, 12);

		when(exerciseMapper.toDTO(benchPress)).thenReturn(benchPressDTO);
		when(exerciseRepository.findById(1L)).thenReturn(Optional.of(benchPress));

		// When
		ExerciseTargetDTO dto = mapper.toDTO(originalEntity);
		ExerciseTargetEntity entityFromDTO = mapper.toEntity(dto);

		// Then
		assertThat(entityFromDTO.getId()).isEqualTo(originalEntity.getId());
		assertThat(entityFromDTO.getSets()).isEqualTo(originalEntity.getSets());
		assertThat(entityFromDTO.getMinReps()).isEqualTo(originalEntity.getMinReps());
		assertThat(entityFromDTO.getMaxReps()).isEqualTo(originalEntity.getMaxReps());
		assertThat(entityFromDTO.getExercise()).isEqualTo(originalEntity.getExercise());
	}
}
