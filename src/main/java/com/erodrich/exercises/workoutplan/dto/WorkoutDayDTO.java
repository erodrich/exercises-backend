package com.erodrich.exercises.workoutplan.dto;

import java.util.List;

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
public class WorkoutDayDTO {
	private Long id;
	private String description;
	private Long workoutPlanId;
	private List<ExerciseTargetDTO> exercises;
}
