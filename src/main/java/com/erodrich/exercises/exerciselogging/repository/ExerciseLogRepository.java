package com.erodrich.exercises.exerciselogging.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLogEntity, Long> {
	List<ExerciseLogEntity> findByUserId(Long userId);
}
