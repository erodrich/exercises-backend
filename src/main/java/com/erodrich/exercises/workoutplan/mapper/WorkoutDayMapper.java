package com.erodrich.exercises.workoutplan.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.workoutplan.dto.ExerciseTargetDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutDayDTO;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkoutDayMapper {
	
	private final ExerciseTargetMapper exerciseTargetMapper;
	
	public WorkoutDayDTO toDTO(WorkoutDayEntity entity) {
		if (entity == null) {
			return null;
		}
		
		List<ExerciseTargetDTO> exerciseDTOs = entity.getExerciseTargetEntityList() != null
				? entity.getExerciseTargetEntityList().stream()
						.map(exerciseTargetMapper::toDTO)
						.collect(Collectors.toList())
				: Collections.emptyList();
		
		Long workoutPlanId = entity.getWorkoutPlanEntityList() != null
				? entity.getWorkoutPlanEntityList().getId()
				: null;
		
		return WorkoutDayDTO.builder()
				.id(entity.getId())
				.description(entity.getDescription())
				.workoutPlanId(workoutPlanId)
				.exercises(exerciseDTOs)
				.build();
	}
	
	public WorkoutDayEntity toEntity(WorkoutDayDTO dto) {
		if (dto == null) {
			return null;
		}
		
		WorkoutDayEntity entity = new WorkoutDayEntity();
		entity.setId(dto.getId());
		entity.setDescription(dto.getDescription());
		
		// Map exercise targets
		if (dto.getExercises() != null) {
			List<ExerciseTargetEntity> exerciseEntities = dto.getExercises().stream()
					.map(exerciseTargetMapper::toEntity)
					.collect(Collectors.toList());
			entity.setExerciseTargetEntityList(exerciseEntities);
		}
		
		// Note: WorkoutPlan relationship is set from the parent side
		
		return entity;
	}
}
