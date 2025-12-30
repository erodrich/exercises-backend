package com.erodrich.exercises.workoutplan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erodrich.exercises.user.repository.UserRepository;
import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.entity.WorkoutPlanEntity;
import com.erodrich.exercises.workoutplan.mapper.WorkoutPlanMapper;
import com.erodrich.exercises.workoutplan.repository.WorkoutPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {
	private final WorkoutPlanRepository workoutPlanRepository;
	private final WorkoutPlanMapper mapper;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<WorkoutPlanDTO> getAllWorkoutPlans(Long userId) {
		return workoutPlanRepository.findByUserId(userId).stream()
				.map(mapper::toDTO)
				.toList();
	}

	@Transactional
	public WorkoutPlanDTO saveWorkoutPlan(Long userId, WorkoutPlanDTO workoutPlan) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		var workoutPlanEntity = mapper.toEntity(user, workoutPlan);
		var saveWorkoutPlan = workoutPlanRepository.save(workoutPlanEntity);

		return mapper.toDTO(saveWorkoutPlan);
	}

	@Transactional(readOnly = true)
	public Optional<WorkoutPlanDTO> getWorkoutPlanById(Long userId, Long workoutPlanId) {
		return workoutPlanRepository.findByUserIdAndId(userId, workoutPlanId)
				.map(mapper::toDTO);
	}

	@Transactional
	public WorkoutPlanDTO updateWorkoutPlan(Long userId, Long workoutPlanId, WorkoutPlanDTO workoutPlanDTO) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		var workoutPlanFound = workoutPlanRepository.findByUserIdAndId(userId, workoutPlanId)
				.orElseThrow(() -> new IllegalArgumentException("User's workout plan not found"));

		WorkoutPlanEntity workoutPlanEntity = mapper.toEntity(user, workoutPlanDTO);
		workoutPlanEntity.setId(workoutPlanFound.getId());
		var updatedWorkoutPlan = workoutPlanRepository.save(workoutPlanEntity);

		return mapper.toDTO(updatedWorkoutPlan);
	}

	@Transactional
	public void deleteWorkoutPlan(Long userId, Long workoutPlanId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		workoutPlanRepository.findByUserIdAndId(userId, workoutPlanId)
				.orElseThrow(() -> new IllegalArgumentException("User's workout plan not found"));

		workoutPlanRepository.deleteById(workoutPlanId);
	}
}
