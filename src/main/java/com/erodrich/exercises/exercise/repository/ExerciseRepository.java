package com.erodrich.exercises.exercise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;

public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {
	Optional<ExerciseEntity> findByNameAndGroup(String name, MuscleGroup group);
}
