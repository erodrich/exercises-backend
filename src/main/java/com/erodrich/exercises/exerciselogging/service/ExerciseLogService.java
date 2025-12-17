package com.erodrich.exercises.exerciselogging.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseSetDTO;
import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;
import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;
import com.erodrich.exercises.exerciselogging.mapper.ExerciseLogMapper;
import com.erodrich.exercises.exerciselogging.repository.ExerciseLogRepository;
import com.erodrich.exercises.exerciselogging.repository.ExerciseSetRepository;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseLogService {

	private final ExerciseLogRepository exerciseLogRepository;
	private final ExerciseRepository exerciseRepository;
	private final ExerciseSetRepository exerciseSetRepository;
	private final UserRepository userRepository;
	private final ExerciseLogMapper mapper;

	@Transactional
	public List<ExerciseLogDTO> saveLogs(Long userId, List<ExerciseLogDTO> logDTOs) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		List<ExerciseLogEntity> entities = logDTOs.stream()
				.map(dto -> convertAndPrepareEntity(dto, user))
				.collect(Collectors.toList());

		List<ExerciseLogEntity> savedEntities = exerciseLogRepository.saveAll(entities);

		return savedEntities.stream()
				.map(mapper::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ExerciseLogDTO> getAllLogs(Long userId) {
		return exerciseLogRepository.findByUserId(userId).stream()
				.map(mapper::toDTO)
				.collect(Collectors.toList());
	}

	private ExerciseLogEntity convertAndPrepareEntity(ExerciseLogDTO dto, UserEntity user) {
		ExerciseLogEntity logEntity = mapper.toEntity(dto);
		logEntity.setUser(user);

		// Find or create exercise
		ExerciseEntity exercise = findOrCreateExercise(
				dto.getExercise().getName(),
				MuscleGroup.valueOf(dto.getExercise().getGroup().toUpperCase())
		);
		logEntity.setExercise(exercise);

		// Persist sets first
		Set<ExerciseSetEntity> persistedSets = new HashSet<>();
		for (ExerciseSetDTO setDTO : dto.getSets()) {
			ExerciseSetEntity setEntity = new ExerciseSetEntity();
			setEntity.setWeight(setDTO.getWeight());
			setEntity.setReps(setDTO.getReps());
			ExerciseSetEntity savedSet = exerciseSetRepository.save(setEntity);
			persistedSets.add(savedSet);
		}
		logEntity.setSets(persistedSets);

		return logEntity;
	}

	private ExerciseEntity findOrCreateExercise(String name, MuscleGroup group) {
		return exerciseRepository.findByNameAndGroup(name, group)
				.orElseGet(() -> {
					ExerciseEntity newExercise = new ExerciseEntity();
					newExercise.setName(name);
					newExercise.setGroup(group);
					return exerciseRepository.save(newExercise);
				});
	}
}
