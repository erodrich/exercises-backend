package com.erodrich.exercises.workoutplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;

public interface WorkoutDayRepository extends JpaRepository<WorkoutDayEntity, Long> {
}
