package com.erodrich.exercises.exerciselogging;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.service.ExerciseLogService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/users/{userId}/logs")
public class ExerciseLoggingBoundary {

	private final ExerciseLogService service;

	@GetMapping
	public List<ExerciseLogDTO> getAllLogs(@PathVariable Long userId) {
		return service.getAllLogs(userId);
	}

	@PostMapping
	public List<ExerciseLogDTO> createLogs(@PathVariable Long userId, @RequestBody List<ExerciseLogDTO> logs) {
		return service.saveLogs(userId, logs);
	}
}
