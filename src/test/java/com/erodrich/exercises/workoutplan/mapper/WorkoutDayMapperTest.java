package com.erodrich.exercises.workoutplan.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.workoutplan.WorkoutPlanTestDataBuilder;
import com.erodrich.exercises.workoutplan.dto.ExerciseTargetDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutDayDTO;
import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

@ExtendWith(MockitoExtension.class)
class WorkoutDayMapperTest {

	@Mock
	private ExerciseTargetMapper exerciseTargetMapper;

	private WorkoutDayMapper mapper;

	private MuscleGroupEntity chest;
	private ExerciseEntity benchPress;
	private ExerciseDTO benchPressDTO;
	private ExerciseTargetEntity target1;
	private ExerciseTargetDTO targetDTO1;

	@BeforeEach
	void setUp() {
		mapper = new WorkoutDayMapper(exerciseTargetMapper);

		chest = WorkoutPlanTestDataBuilder.createMuscleGroup(1L, "CHEST");
		benchPress = WorkoutPlanTestDataBuilder.createExercise(1L, "Bench Press", chest);
		benchPressDTO = WorkoutPlanTestDataBuilder.createExerciseDTO(1L, "Bench Press", "CHEST");

		target1 = WorkoutPlanTestDataBuilder.createExerciseTarget(1L, benchPress, 4, 8, 12);
		targetDTO1 = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(1L, benchPressDTO, 4, 8, 12);
	}

	@Test
	void toDTO_withValidEntity_shouldMapAllFields() {
		// Given
		WorkoutDayEntity entity = WorkoutPlanTestDataBuilder.createWorkoutDayWithExercises(
				1L, "Push Day", target1);

		when(exerciseTargetMapper.toDTO(target1)).thenReturn(targetDTO1);

		// When
		WorkoutDayDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getDescription()).isEqualTo("Push Day");
		assertThat(dto.getExercises()).hasSize(1);
	}

	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		WorkoutDayDTO dto = mapper.toDTO(null);

		// Then
		assertThat(dto).isNull();
	}

	@Test
	void toDTO_withEmptyExerciseList_shouldReturnEmptyList() {
		// Given
		WorkoutDayEntity entity = WorkoutPlanTestDataBuilder.createWorkoutDay(
				1L, "Rest Day", new ArrayList<>());

		// When
		WorkoutDayDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getExercises()).isEmpty();
	}

	@Test
	void toDTO_withMultipleExercises_shouldMapAll() {
		// Given
		ExerciseEntity squat = WorkoutPlanTestDataBuilder.createExercise(2L, "Squat", chest);
		ExerciseTargetEntity target2 = WorkoutPlanTestDataBuilder.createExerciseTarget(2L, squat, 3, 6, 10);

		ExerciseDTO squatDTO = WorkoutPlanTestDataBuilder.createExerciseDTO(2L, "Squat", "LEGS");
		ExerciseTargetDTO targetDTO2 = WorkoutPlanTestDataBuilder.createExerciseTargetDTO(2L, squatDTO, 3, 6, 10);

		WorkoutDayEntity entity = WorkoutPlanTestDataBuilder.createWorkoutDayWithExercises(
				1L, "Full Body", target1, target2);

		when(exerciseTargetMapper.toDTO(target1)).thenReturn(targetDTO1);
		when(exerciseTargetMapper.toDTO(target2)).thenReturn(targetDTO2);

		// When
		WorkoutDayDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getExercises()).hasSize(2);
		assertThat(dto.getExercises()).containsExactly(targetDTO1, targetDTO2);
	}

	@Test
	void toDTO_withNullWorkoutPlan_shouldHandleGracefully() {
		// Given
		WorkoutDayEntity entity = WorkoutPlanTestDataBuilder.createSimpleWorkoutDay(1L, "Test Day");
		// workoutPlanEntityList is null

		// When
		WorkoutDayDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getWorkoutPlanId()).isNull();
	}

	@Test
	void toDTO_withWorkoutPlan_shouldExtractWorkoutPlanId() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(1L, "testuser", "test@example.com");
		WorkoutPlanEntity plan = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				10L, "Test Plan", 12, DurationUnitEnum.WEEKS, true, user, new ArrayList<>());

		WorkoutDayEntity entity = WorkoutPlanTestDataBuilder.createSimpleWorkoutDay(1L, "Test Day");
		entity.setWorkoutPlanEntityList(plan);

		// When
		WorkoutDayDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getWorkoutPlanId()).isEqualTo(10L);
	}

	@Test
	void toEntity_withValidDTO_shouldMapAllFields() {
		// Given
		WorkoutDayDTO dto = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(
				1L, "Push Day", 10L, Arrays.asList(targetDTO1));

		when(exerciseTargetMapper.toEntity(targetDTO1)).thenReturn(target1);

		// When
		WorkoutDayEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1L);
		assertThat(entity.getDescription()).isEqualTo("Push Day");
		assertThat(entity.getExerciseTargetEntityList()).hasSize(1);
	}

	@Test
	void toEntity_withNullDTO_shouldReturnNull() {
		// When
		WorkoutDayEntity entity = mapper.toEntity(null);

		// Then
		assertThat(entity).isNull();
	}

	@Test
	void toEntity_withExercises_shouldMapUsingExerciseTargetMapper() {
		// Given
		WorkoutDayDTO dto = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(
				1L, "Push Day", 10L, Arrays.asList(targetDTO1));

		when(exerciseTargetMapper.toEntity(targetDTO1)).thenReturn(target1);

		// When
		WorkoutDayEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity.getExerciseTargetEntityList()).containsExactly(target1);
	}

	@Test
	void toEntity_withNullExercises_shouldHandleGracefully() {
		// Given
		WorkoutDayDTO dto = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(
				1L, "Rest Day", null, null);

		// When
		WorkoutDayEntity entity = mapper.toEntity(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getExerciseTargetEntityList()).isEmpty();
	}

	@Test
	void bidirectionalMapping_shouldPreserveData() {
		// Given
		WorkoutDayEntity originalEntity = WorkoutPlanTestDataBuilder.createWorkoutDayWithExercises(
				1L, "Push Day", target1);

		when(exerciseTargetMapper.toDTO(target1)).thenReturn(targetDTO1);
		when(exerciseTargetMapper.toEntity(targetDTO1)).thenReturn(target1);

		// When
		WorkoutDayDTO dto = mapper.toDTO(originalEntity);
		WorkoutDayEntity entityFromDTO = mapper.toEntity(dto);

		// Then
		assertThat(entityFromDTO.getId()).isEqualTo(originalEntity.getId());
		assertThat(entityFromDTO.getDescription()).isEqualTo(originalEntity.getDescription());
		assertThat(entityFromDTO.getExerciseTargetEntityList()).hasSize(1);
	}
}
