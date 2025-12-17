package com.erodrich.exercises.exercise;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.exercise.dto.ExerciseDTO;
import com.erodrich.exercises.exercise.service.ExerciseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/admin/exercises")
public class ExerciseBoundary {
	
	private final ExerciseService exerciseService;
	
	@GetMapping
	public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
		return ResponseEntity.ok(exerciseService.getAllExercises());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Long id) {
		try {
			ExerciseDTO exercise = exerciseService.getExerciseById(id);
			return ResponseEntity.ok(exercise);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PostMapping
	public ResponseEntity<ExerciseDTO> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
		try {
			ExerciseDTO created = exerciseService.createExercise(exerciseDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable Long id, @RequestBody ExerciseDTO exerciseDTO) {
		try {
			ExerciseDTO updated = exerciseService.updateExercise(id, exerciseDTO);
			return ResponseEntity.ok(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
		try {
			exerciseService.deleteExercise(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
