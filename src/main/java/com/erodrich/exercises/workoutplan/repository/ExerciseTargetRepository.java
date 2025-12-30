package com.erodrich.exercises.workoutplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;

public interface ExerciseTargetRepository extends JpaRepository<ExerciseTargetEntity, Long> {
}
