package com.erodrich.exercises.exercise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;

public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {
	
	/**
	 * Find an exercise by name and muscle group entity
	 * @param name the exercise name
	 * @param muscleGroup the muscle group entity
	 * @return Optional containing the exercise if found
	 */
	Optional<ExerciseEntity> findByNameAndMuscleGroup(String name, MuscleGroupEntity muscleGroup);
	
	/**
	 * Count exercises by muscle group
	 * @param muscleGroup the muscle group entity
	 * @return the count of exercises in this muscle group
	 */
	long countByMuscleGroup(MuscleGroupEntity muscleGroup);
}
