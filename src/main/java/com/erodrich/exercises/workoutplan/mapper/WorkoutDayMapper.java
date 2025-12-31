package com.erodrich.exercises.workoutplan.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
				.toList()
				: Collections.emptyList();

		Long workoutPlanId = entity.getWorkoutPlanEntity() != null
				? entity.getWorkoutPlanEntity().getId()
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
		return toEntity(dto, null);
	}


	public WorkoutDayEntity toEntity(WorkoutDayDTO dto, WorkoutDayEntity entity) {
		if (dto == null) {
			return null;
		}
		if (entity == null) {
			entity = new WorkoutDayEntity();
		}
		entity.setId(dto.getId());
		entity.setDescription(dto.getDescription());

		// Map exercise targets
		if (dto.getExercises() != null) {
			// No exercise targets
			if (entity.getExerciseTargetEntityList() == null || entity.getExerciseTargetEntityList().isEmpty()) {
				var exerciseEntities = new ArrayList<ExerciseTargetEntity>();
				for (var exerciseTargetDto : dto.getExercises()) {
					if (exerciseTargetDto.getId() == null) {
						exerciseEntities.add(exerciseTargetMapper.toEntity(exerciseTargetDto));
					}
				}
				entity.setExerciseTargetEntityList(exerciseEntities);
			} else {
				// Existing exercise targets
				var newExerciseTargetList = new ArrayList<ExerciseTargetEntity>();
				for (var exerciseTargetDto : dto.getExercises()) {
					if (exerciseTargetDto.getId() != null) {
						entity.getExerciseTargetEntityList().stream()
								.filter(exerciseTargetEntity -> Objects.equals(exerciseTargetEntity.getId(), exerciseTargetDto.getId()))
								.findFirst()
								.ifPresent(exerciseTargetEntity -> newExerciseTargetList.add(
										exerciseTargetMapper.toEntity(exerciseTargetDto, exerciseTargetEntity)));
					} else {
						newExerciseTargetList.add(exerciseTargetMapper.toEntity(exerciseTargetDto));
					}
				}
				entity.getExerciseTargetEntityList().clear();
				entity.getExerciseTargetEntityList().addAll(newExerciseTargetList);
			}
		}

		// Note: WorkoutPlan relationship is set from the parent side

		return entity;
	}
}
