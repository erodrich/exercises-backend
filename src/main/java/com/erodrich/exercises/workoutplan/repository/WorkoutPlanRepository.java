package com.erodrich.exercises.workoutplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlanEntity, Long> {
	List<WorkoutPlanEntity> findByUserId(Long userId);

	Optional<WorkoutPlanEntity> findByUserIdAndIsActiveTrue(Long userId);

	Optional<WorkoutPlanEntity> findByUserIdAndId(Long userId, Long workoutPlanId);
}
