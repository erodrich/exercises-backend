package com.erodrich.exercises.exerciselogging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSetDTO {
	private Double weight;
	private Integer reps;
}
