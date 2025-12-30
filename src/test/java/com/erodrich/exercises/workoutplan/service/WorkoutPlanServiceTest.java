package com.erodrich.exercises.workoutplan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.workoutplan.WorkoutPlanTestDataBuilder;
import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;
import com.erodrich.exercises.workoutplan.mapper.WorkoutPlanMapper;
import com.erodrich.exercises.workoutplan.repository.WorkoutPlanRepository;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanServiceTest {
	
	@Mock
	private WorkoutPlanRepository workoutPlanRepository;
	
	@Mock
	private WorkoutPlanMapper workoutPlanMapper;
	
	@InjectMocks
	private WorkoutPlanService workoutPlanService;
	
	private UserEntity user;
	private WorkoutPlanEntity plan1;
	private WorkoutPlanEntity plan2;
	private WorkoutPlanDTO planDTO1;
	private WorkoutPlanDTO planDTO2;
	
	@BeforeEach
	void setUp() {
		user = WorkoutPlanTestDataBuilder.createUser(1L, "testuser", "test@example.com");
		
		plan1 = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				1L, "PPL Program", 12, DurationUnitEnum.WEEKS, true, user, Collections.emptyList());
		
		plan2 = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				2L, "Upper Lower", 8, DurationUnitEnum.WEEKS, false, user, Collections.emptyList());
		
		planDTO1 = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				1L, "PPL Program", 12, DurationUnitEnum.WEEKS, true, 1L, Collections.emptyList());
		
		planDTO2 = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				2L, "Upper Lower", 8, DurationUnitEnum.WEEKS, false, 1L, Collections.emptyList());
	}
	
	@Test
	void getAllWorkoutPlans_whenUserHasPlans_shouldReturnDTOs() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1, plan2));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		when(workoutPlanMapper.toDTO(plan2)).thenReturn(planDTO2);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(planDTO1, planDTO2);
	}
	
	@Test
	void getAllWorkoutPlans_whenUserHasNoPlans_shouldReturnEmptyList() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).isEmpty();
	}
	
	@Test
	void getAllWorkoutPlans_shouldUseMapperForConversion() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		verify(workoutPlanMapper).toDTO(plan1);
		assertThat(result).containsExactly(planDTO1);
	}
	
	@Test
	void getAllWorkoutPlans_shouldCallRepositoryWithCorrectUserId() {
		// Given
		Long userId = 42L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
		
		// When
		workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		verify(workoutPlanRepository).findByUserId(42L);
	}
	
	@Test
	void getAllWorkoutPlans_withMultiplePlans_shouldMapAll() {
		// Given
		Long userId = 1L;
		WorkoutPlanEntity plan3 = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				3L, "Full Body", 4, DurationUnitEnum.WEEKS, true, user, Collections.emptyList());
		
		WorkoutPlanDTO planDTO3 = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				3L, "Full Body", 4, DurationUnitEnum.WEEKS, true, 1L, Collections.emptyList());
		
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1, plan2, plan3));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		when(workoutPlanMapper.toDTO(plan2)).thenReturn(planDTO2);
		when(workoutPlanMapper.toDTO(plan3)).thenReturn(planDTO3);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(3);
		assertThat(result).extracting(WorkoutPlanDTO::getName)
				.containsExactly("PPL Program", "Upper Lower", "Full Body");
	}
	
	@Test
	void getAllWorkoutPlans_shouldPreserveOrder() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan2, plan1));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		when(workoutPlanMapper.toDTO(plan2)).thenReturn(planDTO2);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(planDTO2, planDTO1);
	}
	
	@Test
	void getAllWorkoutPlans_shouldHandleActivePlans() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).isActive()).isTrue();
	}
	
	@Test
	void getAllWorkoutPlans_shouldHandleInactivePlans() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan2));
		when(workoutPlanMapper.toDTO(plan2)).thenReturn(planDTO2);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).isActive()).isFalse();
	}
	
	@Test
	void getAllWorkoutPlans_shouldHandleDifferentDurationUnits() {
		// Given
		Long userId = 1L;
		WorkoutPlanEntity planMonths = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				3L, "Long Plan", 3, DurationUnitEnum.MONTHS, true, user, Collections.emptyList());
		
		WorkoutPlanDTO planMonthsDTO = WorkoutPlanTestDataBuilder.createWorkoutPlanDTO(
				3L, "Long Plan", 3, DurationUnitEnum.MONTHS, true, 1L, Collections.emptyList());
		
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1, planMonths));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		when(workoutPlanMapper.toDTO(planMonths)).thenReturn(planMonthsDTO);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS);
		assertThat(result.get(1).getDurationUnit()).isEqualTo(DurationUnitEnum.MONTHS);
	}
	
	@Test
	void getAllWorkoutPlans_shouldMapEachPlanIndependently() {
		// Given
		Long userId = 1L;
		when(workoutPlanRepository.findByUserId(userId)).thenReturn(Arrays.asList(plan1, plan2));
		when(workoutPlanMapper.toDTO(plan1)).thenReturn(planDTO1);
		when(workoutPlanMapper.toDTO(plan2)).thenReturn(planDTO2);
		
		// When
		List<WorkoutPlanDTO> result = workoutPlanService.getAllWorkoutPlans(userId);
		
		// Then
		verify(workoutPlanMapper).toDTO(plan1);
		verify(workoutPlanMapper).toDTO(plan2);
		assertThat(result.get(0).getId()).isEqualTo(1L);
		assertThat(result.get(1).getId()).isEqualTo(2L);
	}
}
