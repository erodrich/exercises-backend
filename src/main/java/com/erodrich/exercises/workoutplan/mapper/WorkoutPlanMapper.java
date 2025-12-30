package com.erodrich.exercises.workoutplan.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.workoutplan.dto.WorkoutDayDTO;
import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.entity.WorkoutDayEntity;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkoutPlanMapper {

	private final WorkoutDayMapper workoutDayMapper;

	public WorkoutPlanDTO toDTO(WorkoutPlanEntity entity) {
		if (entity == null) {
			return null;
		}

		List<WorkoutDayDTO> workoutDayDTOs = entity.getWorkoutDayEntityList() != null
				? entity.getWorkoutDayEntityList().stream()
				.map(workoutDayMapper::toDTO)
				.toList()
				: Collections.emptyList();

		return WorkoutPlanDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.duration(entity.getDuration())
				.durationUnit(entity.getDurationUnit())
				.isActive(entity.isActive())
				.workoutDayDTOList(workoutDayDTOs)
				.build();
	}

	public WorkoutPlanEntity toEntity(UserEntity user, WorkoutPlanDTO dto) {
		if (dto == null) {
			return null;
		}

		WorkoutPlanEntity entity = new WorkoutPlanEntity();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setDuration(dto.getDuration());
		entity.setDurationUnit(dto.getDurationUnit());
		entity.setActive(dto.isActive());
		entity.setUser(user);

		// Map workout days
		if (dto.getWorkoutDayDTOList() != null) {
			List<WorkoutDayEntity> workoutDayEntities = dto.getWorkoutDayDTOList().stream()
					.map(workoutDayMapper::toEntity)
					.toList();
			entity.setWorkoutDayEntityList(workoutDayEntities);
		}

		return entity;
	}
}
