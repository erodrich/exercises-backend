package com.erodrich.exercises.workoutplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.user.entity.Role;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.workoutplan.dto.ExerciseTargetDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutDayDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

/**
 * Test data builder for WorkoutPlan tests
 * Provides fluent API for creating test entities and DTOs
 */
public class WorkoutPlanTestDataBuilder {

	public static UserEntity createUser(Long id, String username, String email) {
		UserEntity user = new UserEntity();
		user.setId(id);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword("hashedPassword");
		user.setRole(Role.USER);
		return user;
	}

	public static MuscleGroupEntity createMuscleGroup(Long id, String name) {
		return new MuscleGroupEntity(id, name, name + " exercises");
	}

	public static ExerciseEntity createExercise(Long id, String name, MuscleGroupEntity muscleGroup) {
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setId(id);
		exercise.setName(name);
		exercise.setMuscleGroup(muscleGroup);
		return exercise;
	}

	public static ExerciseTargetEntity createExerciseTarget(Long id, ExerciseEntity exercise,
			Integer sets, Integer minReps, Integer maxReps) {
		ExerciseTargetEntity target = new ExerciseTargetEntity();
		target.setId(id);
		target.setExercise(exercise);
		target.setSets(sets);
		target.setMinReps(minReps);
		target.setMaxReps(maxReps);
		return target;
	}

	public static WorkoutDayEntity createWorkoutDay(Long id, String description,
			List<ExerciseTargetEntity> exercises) {
		WorkoutDayEntity day = new WorkoutDayEntity();
		day.setId(id);
		day.setDescription(description);
		day.setExerciseTargetEntityList(exercises != null ? exercises : new ArrayList<>());
		return day;
	}

	public static WorkoutPlanEntity createWorkoutPlan(Long id, String name, Integer duration,
			DurationUnitEnum unit, boolean active, UserEntity user, List<WorkoutDayEntity> days) {
		WorkoutPlanEntity plan = new WorkoutPlanEntity();
		plan.setId(id);
		plan.setName(name);
		plan.setDuration(duration);
		plan.setDurationUnit(unit);
		plan.setActive(active);
		plan.setUser(user);
		plan.setWorkoutDayEntityList(days != null ? days : new ArrayList<>());
		return plan;
	}

	public static ExerciseDTO createExerciseDTO(Long id, String name, String group) {
		ExerciseDTO dto = new ExerciseDTO();
		dto.setId(id);
		dto.setName(name);
		dto.setGroup(group);
		return dto;
	}

	public static ExerciseTargetDTO createExerciseTargetDTO(Long id, ExerciseDTO exercise,
			Integer sets, Integer minReps, Integer maxReps) {
		return ExerciseTargetDTO.builder()
				.id(id)
				.exercise(exercise)
				.sets(sets)
				.minReps(minReps)
				.maxReps(maxReps)
				.build();
	}

	public static WorkoutDayDTO createWorkoutDayDTO(Long id, String description,
			Long workoutPlanId, List<ExerciseTargetDTO> exercises) {
		return WorkoutDayDTO.builder()
				.id(id)
				.description(description)
				.workoutPlanId(workoutPlanId)
				.exercises(exercises != null ? exercises : new ArrayList<>())
				.build();
	}

	public static WorkoutPlanDTO createWorkoutPlanDTO(Long id, String name, Integer duration,
			DurationUnitEnum unit, boolean active, Long userId, List<WorkoutDayDTO> days) {
		return WorkoutPlanDTO.builder()
				.id(id)
				.name(name)
				.duration(duration)
				.durationUnit(unit)
				.isActive(active)
				.workoutDayDTOList(days != null ? days : new ArrayList<>())
				.build();
	}

	// Convenience methods for common test scenarios

	public static WorkoutPlanEntity createSimpleWorkoutPlan(Long id, String name, UserEntity user) {
		return createWorkoutPlan(id, name, 12, DurationUnitEnum.WEEKS, true, user, new ArrayList<>());
	}

	public static WorkoutPlanEntity createWorkoutPlanWithDays(Long id, String name, UserEntity user,
			WorkoutDayEntity... days) {
		return createWorkoutPlan(id, name, 12, DurationUnitEnum.WEEKS, true, user, Arrays.asList(days));
	}

	public static WorkoutDayEntity createSimpleWorkoutDay(Long id, String description) {
		return createWorkoutDay(id, description, new ArrayList<>());
	}

	public static WorkoutDayEntity createWorkoutDayWithExercises(Long id, String description,
			ExerciseTargetEntity... exercises) {
		return createWorkoutDay(id, description, Arrays.asList(exercises));
	}
}
