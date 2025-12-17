package com.erodrich.exercises.exercise.mapper;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;

@Component
public class ExerciseMapper {
	
	public ExerciseEntity toEntity(ExerciseDTO dto) {
		if (dto == null) {
			return null;
		}
		
		ExerciseEntity entity = new ExerciseEntity();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setGroup(MuscleGroup.valueOf(dto.getGroup().toUpperCase()));
		
		return entity;
	}
	
	public ExerciseDTO toDTO(ExerciseEntity entity) {
		if (entity == null) {
			return null;
		}
		
		ExerciseDTO dto = new ExerciseDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setGroup(entity.getGroup().name());
		
		return dto;
	}
}
