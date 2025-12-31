package com.erodrich.exercises.workoutplan.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
				.workoutDays(workoutDayDTOs)
				.build();
	}

	public WorkoutPlanEntity toEntity(UserEntity user, WorkoutPlanDTO dto) {
		if (dto == null) {
			return null;
		}
		return toEntity(user, dto, null);
	}

	public WorkoutPlanEntity toEntity(UserEntity user, WorkoutPlanDTO dto, WorkoutPlanEntity entity) {
		if (dto == null) {
			return null;
		}
		if (entity == null) {
			entity = new WorkoutPlanEntity();
		}

		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setDuration(dto.getDuration());
		entity.setDurationUnit(dto.getDurationUnit());
		entity.setActive(dto.isActive());
		entity.setUser(user);

		// Map workout days
		if (dto.getWorkoutDays() != null) {
			if (entity.getWorkoutDayEntityList() == null) {
				var workoutDayEntities = new ArrayList<WorkoutDayEntity>();
				for (var workoutDayDto : dto.getWorkoutDays()) {
					if (workoutDayDto.getId() == null) {
						workoutDayEntities.add(workoutDayMapper.toEntity(workoutDayDto));
					}
				}
				entity.setWorkoutDayEntityList(workoutDayEntities);
			} else {
				var newWorkoutDayList = new ArrayList<WorkoutDayEntity>();
				for (var workoutDayDto : dto.getWorkoutDays()) {
					if (workoutDayDto.getId() != null) {
						entity.getWorkoutDayEntityList().stream()
								.filter(workoutDayEntity -> Objects.equals(workoutDayEntity.getId(), workoutDayDto.getId()))
								.findFirst()
								.ifPresent(workoutDayEntity -> newWorkoutDayList.add(workoutDayMapper.toEntity(workoutDayDto, workoutDayEntity)));
					} else {
						newWorkoutDayList.add(workoutDayMapper.toEntity(workoutDayDto));
					}
				}
				entity.getWorkoutDayEntityList().clear();
				entity.getWorkoutDayEntityList().addAll(newWorkoutDayList);
			}
		}

		return entity;
	}
}
