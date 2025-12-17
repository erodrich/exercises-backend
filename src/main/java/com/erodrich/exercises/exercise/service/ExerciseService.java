package com.erodrich.exercises.exercise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;
import com.erodrich.exercises.exercise.mapper.ExerciseMapper;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseService {
	
	private final ExerciseRepository exerciseRepository;
	private final ExerciseMapper mapper;
	
	@Transactional(readOnly = true)
	public List<ExerciseDTO> getAllExercises() {
		return exerciseRepository.findAll().stream()
				.map(mapper::toDTO)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public ExerciseDTO getExerciseById(Long id) {
		ExerciseEntity entity = exerciseRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
		return mapper.toDTO(entity);
	}
	
	@Transactional
	public ExerciseDTO createExercise(ExerciseDTO dto) {
		// Validate muscle group
		try {
			MuscleGroup.valueOf(dto.getGroup().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid muscle group: " + dto.getGroup());
		}
		
		// Check if exercise already exists
		if (exerciseRepository.findByNameAndGroup(
				dto.getName(), 
				MuscleGroup.valueOf(dto.getGroup().toUpperCase())).isPresent()) {
			throw new IllegalArgumentException("Exercise already exists");
		}
		
		ExerciseEntity entity = mapper.toEntity(dto);
		entity.setId(null); // Ensure new entity
		ExerciseEntity saved = exerciseRepository.save(entity);
		
		return mapper.toDTO(saved);
	}
	
	@Transactional
	public ExerciseDTO updateExercise(Long id, ExerciseDTO dto) {
		ExerciseEntity existing = exerciseRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
		
		// Validate muscle group
		try {
			MuscleGroup.valueOf(dto.getGroup().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid muscle group: " + dto.getGroup());
		}
		
		existing.setName(dto.getName());
		existing.setGroup(MuscleGroup.valueOf(dto.getGroup().toUpperCase()));
		
		ExerciseEntity updated = exerciseRepository.save(existing);
		return mapper.toDTO(updated);
	}
	
	@Transactional
	public void deleteExercise(Long id) {
		if (!exerciseRepository.existsById(id)) {
			throw new IllegalArgumentException("Exercise not found");
		}
		exerciseRepository.deleteById(id);
	}
}
