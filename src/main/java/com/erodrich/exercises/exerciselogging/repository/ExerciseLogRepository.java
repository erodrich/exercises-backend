package com.erodrich.exercises.exerciselogging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLogEntity, Long> {
	List<ExerciseLogEntity> findByUserId(Long userId);
	
	Optional<ExerciseLogEntity> findFirstByUserIdAndExerciseIdOrderByDateDesc(Long userId, Long exerciseId);
}
