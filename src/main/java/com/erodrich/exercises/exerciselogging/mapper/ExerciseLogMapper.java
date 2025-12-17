package com.erodrich.exercises.exerciselogging.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;
import com.erodrich.exercises.exerciselogging.dto.ExerciseDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseSetDTO;
import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;
import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;

@Component
public class ExerciseLogMapper {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

	public ExerciseLogEntity toEntity(ExerciseLogDTO dto) {
		if (dto == null) {
			return null;
		}

		ExerciseLogEntity entity = new ExerciseLogEntity();
		entity.setDate(parseTimestamp(dto.getTimestamp()));
		entity.setHasFailed(dto.getFailure() != null && dto.getFailure());
		entity.setExercise(toExerciseEntity(dto.getExercise()));
		entity.setSets(toExerciseSetEntities(dto.getSets()));

		return entity;
	}

	public ExerciseLogDTO toDTO(ExerciseLogEntity entity) {
		if (entity == null) {
			return null;
		}

		ExerciseLogDTO dto = new ExerciseLogDTO();
		dto.setTimestamp(formatTimestamp(entity.getDate()));
		dto.setFailure(entity.isHasFailed());
		dto.setExercise(toExerciseDTO(entity.getExercise()));
		dto.setSets(entity.getSets().stream()
				.map(this::toExerciseSetDTO)
				.collect(Collectors.toList()));

		return dto;
	}

	private ExerciseEntity toExerciseEntity(ExerciseDTO dto) {
		if (dto == null) {
			return null;
		}

		ExerciseEntity entity = new ExerciseEntity();
		entity.setName(dto.getName());
		entity.setGroup(MuscleGroup.valueOf(dto.getGroup().toUpperCase()));

		return entity;
	}

	private ExerciseDTO toExerciseDTO(ExerciseEntity entity) {
		if (entity == null) {
			return null;
		}

		ExerciseDTO dto = new ExerciseDTO();
		dto.setName(entity.getName());
		dto.setGroup(entity.getGroup().name());

		return dto;
	}

	private Set<ExerciseSetEntity> toExerciseSetEntities(java.util.List<ExerciseSetDTO> dtos) {
		if (dtos == null) {
			return new HashSet<>();
		}

		return dtos.stream()
				.map(this::toExerciseSetEntity)
				.collect(Collectors.toSet());
	}

	private ExerciseSetEntity toExerciseSetEntity(ExerciseSetDTO dto) {
		if (dto == null) {
			return null;
		}

		ExerciseSetEntity entity = new ExerciseSetEntity();
		entity.setWeight(dto.getWeight());
		entity.setReps(dto.getReps());

		return entity;
	}

	private ExerciseSetDTO toExerciseSetDTO(ExerciseSetEntity entity) {
		if (entity == null) {
			return null;
		}

		ExerciseSetDTO dto = new ExerciseSetDTO();
		dto.setWeight(entity.getWeight());
		dto.setReps(entity.getReps());

		return dto;
	}

	private LocalDateTime parseTimestamp(String timestamp) {
		if (timestamp == null || timestamp.isEmpty()) {
			return LocalDateTime.now();
		}
		return LocalDateTime.parse(timestamp, FORMATTER);
	}

	private String formatTimestamp(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.format(FORMATTER);
	}
}
