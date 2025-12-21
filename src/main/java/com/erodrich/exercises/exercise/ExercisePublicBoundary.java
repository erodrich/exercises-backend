package com.erodrich.exercises.exercise;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.service.ExerciseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/exercises")
public class ExercisePublicBoundary {
	private final ExerciseService exerciseService;

	@GetMapping
	public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
		var exercises = exerciseService.getAllExercises();
		return ok(exercises);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExerciseDTO> getExerciseById(Long id) {
		var exercise = exerciseService.getExerciseById(id);
		return ResponseEntity.ok(exercise);
	}
}
