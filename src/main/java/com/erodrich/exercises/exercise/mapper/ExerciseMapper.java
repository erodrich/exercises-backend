package com.erodrich.exercises.exercise.mapper;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;

@Component
public class ExerciseMapper {
	
	private final MuscleGroupRepository muscleGroupRepository;
	
	public ExerciseMapper(MuscleGroupRepository muscleGroupRepository) {
		this.muscleGroupRepository = muscleGroupRepository;
	}
	
	public ExerciseEntity toEntity(ExerciseDTO dto) {
		if (dto == null) {
			return null;
		}
		
		ExerciseEntity entity = new ExerciseEntity();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		
		// Look up the MuscleGroupEntity by name
		MuscleGroupEntity muscleGroup = muscleGroupRepository
				.findByNameIgnoreCase(dto.getGroup())
				.orElseThrow(() -> new IllegalArgumentException(
						"Invalid muscle group: " + dto.getGroup()));
		entity.setMuscleGroup(muscleGroup);
		
		return entity;
	}
	
	public ExerciseDTO toDTO(ExerciseEntity entity) {
		if (entity == null) {
			return null;
		}
		
		ExerciseDTO dto = new ExerciseDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		
		// Convert MuscleGroupEntity to String for DTO
		if (entity.getMuscleGroup() != null) {
			dto.setGroup(entity.getMuscleGroup().getName());
		}
		
		return dto;
	}
}
