package com.erodrich.exercises.workoutplan.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.workoutplan.WorkoutPlanTestDataBuilder;
import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

@DataJpaTest
class WorkoutPlanRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private WorkoutPlanRepository workoutPlanRepository;

	@Test
	void findByUserId_whenUserHasPlans_shouldReturnList() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);

		WorkoutPlanEntity plan1 = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "Plan 1", user);
		WorkoutPlanEntity plan2 = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "Plan 2", user);

		entityManager.persist(plan1);
		entityManager.persist(plan2);
		entityManager.flush();

		// When
		List<WorkoutPlanEntity> found = workoutPlanRepository.findByUserId(user.getId());

		// Then
		assertThat(found).hasSize(2);
		assertThat(found).extracting(WorkoutPlanEntity::getName)
				.containsExactlyInAnyOrder("Plan 1", "Plan 2");
	}

	@Test
	void findByUserId_whenUserHasNoPlans_shouldReturnEmptyList() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);
		entityManager.flush();

		// When
		List<WorkoutPlanEntity> found = workoutPlanRepository.findByUserId(user.getId());

		// Then
		assertThat(found).isEmpty();
	}

	@Test
	void findByUserId_whenUserDoesNotExist_shouldReturnEmptyList() {
		// When
		List<WorkoutPlanEntity> found = workoutPlanRepository.findByUserId(999L);

		// Then
		assertThat(found).isEmpty();
	}

	@Test
	void save_shouldPersistWorkoutPlan() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);

		WorkoutPlanEntity plan = WorkoutPlanTestDataBuilder.createWorkoutPlan(
				null, "Test Plan", 8, DurationUnitEnum.WEEKS, true, user, null);

		// When
		WorkoutPlanEntity saved = workoutPlanRepository.save(plan);

		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("Test Plan");
		assertThat(saved.getDuration()).isEqualTo(8);
		assertThat(saved.getDurationUnit()).isEqualTo(DurationUnitEnum.WEEKS);
		assertThat(saved.isActive()).isTrue();
		assertThat(saved.getUser().getId()).isEqualTo(user.getId());
	}

	@Test
	void save_withNestedWorkoutDays_shouldPersistHierarchy() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);

		MuscleGroupEntity chest = WorkoutPlanTestDataBuilder.createMuscleGroup(null, "CHEST");
		chest = entityManager.persist(chest);

		ExerciseEntity benchPress = WorkoutPlanTestDataBuilder.createExercise(null, "Bench Press", chest);
		benchPress = entityManager.persist(benchPress);

		ExerciseTargetEntity target = WorkoutPlanTestDataBuilder.createExerciseTarget(
				null, benchPress, 4, 8, 12);
		target = entityManager.persist(target);

		WorkoutDayEntity day = WorkoutPlanTestDataBuilder.createWorkoutDayWithExercises(
				null, "Push Day", target);
		day = entityManager.persist(day);

		WorkoutPlanEntity plan = WorkoutPlanTestDataBuilder.createWorkoutPlanWithDays(
				null, "PPL Program", user, day);

		// When
		WorkoutPlanEntity saved = workoutPlanRepository.save(plan);
		entityManager.flush();
		//entityManager.clear();

		// Then
		WorkoutPlanEntity loaded = entityManager.find(WorkoutPlanEntity.class, saved.getId());
		assertThat(loaded).isNotNull();
		assertThat(loaded.getWorkoutDayEntityList()).hasSize(1);
		assertThat(loaded.getWorkoutDayEntityList().get(0).getDescription()).isEqualTo("Push Day");
		assertThat(loaded.getWorkoutDayEntityList().get(0).getExerciseTargetEntityList()).hasSize(1);
	}

	@Test
	void findById_whenExists_shouldReturnWorkoutPlan() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);

		WorkoutPlanEntity plan = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "Test Plan", user);
		plan = entityManager.persist(plan);
		entityManager.flush();

		// When
		Optional<WorkoutPlanEntity> found = workoutPlanRepository.findById(plan.getId());

		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Test Plan");
	}

	@Test
	void delete_shouldRemoveWorkoutPlan() {
		// Given
		UserEntity user = WorkoutPlanTestDataBuilder.createUser(null, "testuser", "test@example.com");
		user = entityManager.persist(user);

		WorkoutPlanEntity plan = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "Test Plan", user);
		plan = entityManager.persist(plan);
		entityManager.flush();

		Long planId = plan.getId();

		// When
		workoutPlanRepository.deleteById(planId);
		entityManager.flush();

		// Then
		Optional<WorkoutPlanEntity> found = workoutPlanRepository.findById(planId);
		assertThat(found).isEmpty();
	}

	@Test
	void findByUserId_shouldReturnOnlyUserPlans() {
		// Given
		UserEntity user1 = WorkoutPlanTestDataBuilder.createUser(null, "user1", "user1@example.com");
		UserEntity user2 = WorkoutPlanTestDataBuilder.createUser(null, "user2", "user2@example.com");
		user1 = entityManager.persist(user1);
		user2 = entityManager.persist(user2);

		WorkoutPlanEntity plan1 = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "User1 Plan", user1);
		WorkoutPlanEntity plan2 = WorkoutPlanTestDataBuilder.createSimpleWorkoutPlan(null, "User2 Plan", user2);

		entityManager.persist(plan1);
		entityManager.persist(plan2);
		entityManager.flush();

		// When
		List<WorkoutPlanEntity> user1Plans = workoutPlanRepository.findByUserId(user1.getId());

		// Then
		assertThat(user1Plans).hasSize(1);
		assertThat(user1Plans.get(0).getName()).isEqualTo("User1 Plan");
		assertThat(user1Plans.get(0).getUser().getId()).isEqualTo(user1.getId());
	}
}
