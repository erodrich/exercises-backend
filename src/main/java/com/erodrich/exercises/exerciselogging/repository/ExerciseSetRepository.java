package com.erodrich.exercises.exerciselogging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSetEntity, Long> {
}
