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

import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.repository.UserRepository;
import com.erodrich.exercises.workoutplan.WorkoutPlanTestDataBuilder;
import com.erodrich.exercises.workoutplan.dto.WorkoutDayDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanMapperTest {

	@Mock
	private WorkoutDayMapper workoutDayMapper;

	@Mock
	private UserRepository userRepository;

	private WorkoutPlanMapper mapper;

	private UserEntity user;
	private WorkoutDayEntity day1;
	private WorkoutDayEntity newDay1;
	private WorkoutDayDTO dayDTO1;
	private WorkoutDayDTO newDayDTO1;

	@BeforeEach
	void setUp() {
		mapper = new WorkoutPlanMapper(workoutDayMapper);

		user = WorkoutPlanTestDataBuilder.createUser(1L, "testuser", "test@example.com");
		day1 = WorkoutPlanTestDataBuilder.createSimpleWorkoutDay(1L, "Push Day");
		dayDTO1 = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(1L, "Push Day", null, new ArrayList<>());
		newDayDTO1 = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(null, "Push Day", null, new ArrayList<>());
		newDay1 = WorkoutPlanTestDataBuilder.createSimpleWorkoutDay(null, "Push Day");
	}

	@Test
	void toDTO_withValidEntity_shouldMapAllFields() {
		// Given
		WorkoutPlanEntity entity = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				1L, "PPL Program", 12, DurationUnitEnum.WEEKS, true, user, new ArrayList<>());

		// When
		WorkoutPlanDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getName()).isEqualTo("PPL Program");
		assertThat(dto.getDuration()).isEqualTo(12);
		assertThat(dto.getDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS);
		assertThat(dto.isActive()).isTrue();
	}

	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		WorkoutPlanDTO dto = mapper.toDTO(null);

		// Then
		assertThat(dto).isNull();
	}

	@Test
	void toDTO_withEmptyWorkoutDays_shouldReturnEmptyList() {
		// Given
		WorkoutPlanEntity entity = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				1L, "Test Plan", 8, DurationUnitEnum.WEEKS, false, user, new ArrayList<>());

		// When
		WorkoutPlanDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getWorkoutDays()).isEmpty();
	}

	@Test
	void toDTO_withMultipleWorkoutDays_shouldMapAll() {
		// Given
		WorkoutDayEntity day2 = WorkoutPlanTestDataBuilder.createSimpleWorkoutDay(2L, "Pull Day");
		WorkoutDayDTO dayDTO2 = WorkoutPlanTestDataBuilder.createWorkoutDayDTO(2L, "Pull Day", null, new ArrayList<>());

		WorkoutPlanEntity entity = WorkoutPlanTestDataBuilder.createWorkoutPlanWithDays(
				1L, "PPL Program", user, day1, day2);

		when(workoutDayMapper.toDTO(day1)).thenReturn(dayDTO1);
		when(workoutDayMapper.toDTO(day2)).thenReturn(dayDTO2);

		// When
		WorkoutPlanDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getWorkoutDays()).hasSize(2);
		assertThat(dto.getWorkoutDays()).containsExactly(dayDTO1, dayDTO2);
	}

	@Test
	void toDTO_withNullUser_shouldHandleGracefully() {
		// Given
		WorkoutPlanEntity entity = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				1L, "Test Plan", 8, DurationUnitEnum.WEEKS, false, null, new ArrayList<>());

		// When
		WorkoutPlanDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
	}

	@Test
	void toDTO_fullHierarchy_shouldMapAllLevels() {
		// Given
		WorkoutPlanEntity entity = WorkoutPlanTestDataBuilder.createWorkoutPlanWithDays(
				1L, "Full Program", user, day1);

		when(workoutDayMapper.toDTO(day1)).thenReturn(dayDTO1);

		// When
		WorkoutPlanDTO dto = mapper.toDTO(entity);

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getName()).isEqualTo("Full Program");
		assertThat(dto.getWorkoutDays()).hasSize(1);
		assertThat(dto.getWorkoutDays().get(0)).isEqualTo(dayDTO1);
	}

	@Test
	void toEntity_withValidDTO_shouldMapAllFields() {
		// Given
		WorkoutPlanDTO dto = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				1L, "PPL Program", 12, DurationUnitEnum.WEEKS, true, 1L, new ArrayList<>());

		// When
		WorkoutPlanEntity entity = mapper.toEntity(user, dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(1L);
		assertThat(entity.getName()).isEqualTo("PPL Program");
		assertThat(entity.getDuration()).isEqualTo(12);
		assertThat(entity.getDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS);
		assertThat(entity.isActive()).isTrue();
		assertThat(entity.getUser()).isEqualTo(user);
	}

	@Test
	void toEntity_withNullDTO_shouldReturnNull() {
		// When
		WorkoutPlanEntity entity = mapper.toEntity(null, null);

		// Then
		assertThat(entity).isNull();
	}

	@Test
	void toEntity_withUserId_shouldLookupUser() {
		// Given
		WorkoutPlanDTO dto = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				null, "Test Plan", 8, DurationUnitEnum.MONTHS, false, 1L, new ArrayList<>());

		// When
		WorkoutPlanEntity entity = mapper.toEntity(user, dto);

		// Then
		assertThat(entity.getUser()).isEqualTo(user);
	}

	@Test
	void toEntity_withWorkoutDays_shouldMapUsingWorkoutDayMapper() {
		// Given
		WorkoutPlanDTO dto = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				1L, "PPL Program", 12, DurationUnitEnum.WEEKS, true, 1L, Arrays.asList(newDayDTO1));

		when(workoutDayMapper.toEntity(newDayDTO1)).thenReturn(newDay1);

		// When
		WorkoutPlanEntity entity = mapper.toEntity(user, dto);

		// Then
		assertThat(entity.getWorkoutDayEntityList()).hasSize(1);
		assertThat(entity.getWorkoutDayEntityList()).containsExactly(newDay1);
	}

	@Test
	void bidirectionalMapping_shouldPreserveData() {
		// Given
		WorkoutPlanEntity originalEntity = WorkoutPlanTestDataBuilder.createWorkoutPlanWithDays(
				1L, "Test Program", user, newDay1);

		when(workoutDayMapper.toDTO(newDay1)).thenReturn(newDayDTO1);
		when(workoutDayMapper.toEntity(newDayDTO1)).thenReturn(newDay1);

		// When
		WorkoutPlanDTO dto = mapper.toDTO(originalEntity);
		WorkoutPlanEntity entityFromDTO = mapper.toEntity(user, dto);

		// Then
		assertThat(entityFromDTO.getId()).isEqualTo(originalEntity.getId());
		assertThat(entityFromDTO.getName()).isEqualTo(originalEntity.getName());
		assertThat(entityFromDTO.getDuration()).isEqualTo(originalEntity.getDuration());
		assertThat(entityFromDTO.getDurationUnit()).isEqualTo(originalEntity.getDurationUnit());
		assertThat(entityFromDTO.isActive()).isEqualTo(originalEntity.isActive());
		assertThat(entityFromDTO.getUser()).isEqualTo(originalEntity.getUser());
		assertThat(entityFromDTO.getWorkoutDayEntityList()).hasSize(1);
	}

	@Test
	void toEntity_withNullUserId_shouldNotLookupUser() {
		// Given
		WorkoutPlanDTO dto = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				1L, "Test Plan", 8, DurationUnitEnum.MONTHS, false, null, new ArrayList<>());

		// When
		WorkoutPlanEntity entity = mapper.toEntity(null, dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getUser()).isNull();
	}
}
