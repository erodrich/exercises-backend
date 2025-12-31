package com.erodrich.exercises.workoutplan.mapper;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.mapper.ExerciseMapper;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.workoutplan.dto.ExerciseTargetDTO;
import com.erodrich.exercises.workoutplan.entity.ExerciseTargetEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExerciseTargetMapper {

	private final ExerciseMapper exerciseMapper;
	private final ExerciseRepository exerciseRepository;

	public ExerciseTargetDTO toDTO(ExerciseTargetEntity entity) {
		if (entity == null) {
			return null;
		}

		ExerciseDTO exerciseDTO = exerciseMapper.toDTO(entity.getExercise());

		return ExerciseTargetDTO.builder()
				.id(entity.getId())
				.exercise(exerciseDTO)
				.sets(entity.getSets())
				.minReps(entity.getMinReps())
				.maxReps(entity.getMaxReps())
				.build();
	}

	public ExerciseTargetEntity toEntity(ExerciseTargetDTO dto) {
		if (dto == null) {
			return null;
		}
		return toEntity(dto, null);
	}

	public ExerciseTargetEntity toEntity(ExerciseTargetDTO dto, ExerciseTargetEntity entity) {
		if (dto == null) {
			return null;
		}
		if (entity == null) {
			entity = new ExerciseTargetEntity();
		}

		entity.setId(dto.getId());
		entity.setSets(dto.getSets());
		entity.setMinReps(dto.getMinReps());
		entity.setMaxReps(dto.getMaxReps());

		// Look up the exercise by ID if present
		if (dto.getExercise() != null && dto.getExercise().getId() != null) {
			ExerciseEntity exercise = exerciseRepository.findById(dto.getExercise().getId())
					.orElseThrow(() -> new IllegalArgumentException(
							"Exercise not found with ID: " + dto.getExercise().getId()));
			entity.setExercise(exercise);
		}

		return entity;
	}
}
