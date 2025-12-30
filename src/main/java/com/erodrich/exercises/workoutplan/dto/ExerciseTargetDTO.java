package com.erodrich.exercises.workoutplan.dto;


import com.erodrich.exercises.exercise.dto.ExerciseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExerciseTargetDTO {
	private Long id;
	private ExerciseDTO exercise;
	private Integer sets;
	private Integer minReps;
	private Integer maxReps;
}
