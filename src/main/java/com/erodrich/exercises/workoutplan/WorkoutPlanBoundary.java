package com.erodrich.exercises.workoutplan;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.workoutplan.dto.WorkoutPlanDTO;
import com.erodrich.exercises.workoutplan.service.WorkoutPlanService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/users/{userId}/workout-plans")
public class WorkoutPlanBoundary {

	private final WorkoutPlanService service;

	@GetMapping
	public List<WorkoutPlanDTO> getAllWorkoutPlans(@PathVariable Long userId) {
		return service.getAllWorkoutPlans(userId);
	}

	@GetMapping("/{workoutPlanId}")
	public ResponseEntity<WorkoutPlanDTO> getWorkoutPlanById(@PathVariable Long userId, @PathVariable Long workoutPlanId) {
		return service.getWorkoutPlanById(userId, workoutPlanId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public WorkoutPlanDTO createWorkoutPlan(@PathVariable Long userId, @RequestBody WorkoutPlanDTO workoutPlan) {
		return service.saveWorkoutPlan(userId, workoutPlan);
	}

	@PutMapping("/{workoutPlanId}")
	public ResponseEntity<WorkoutPlanDTO> updateWorkoutPlan(@PathVariable Long userId, @PathVariable Long workoutPlanId,
			@RequestBody WorkoutPlanDTO workoutPlanDTO) {
		try {
			var updatedWorkoutPlan = service.updateWorkoutPlan(userId, workoutPlanId, workoutPlanDTO);
			return ResponseEntity.ok(updatedWorkoutPlan);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{workoutPlanId}")
	public ResponseEntity<Void> deleteWorkoutPlan(@PathVariable Long userId, @PathVariable Long workoutPlanId) {
		try {
			service.deleteWorkoutPlan(userId, workoutPlanId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
