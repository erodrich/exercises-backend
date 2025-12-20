package com.erodrich.exercises.exerciselogging.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogDTO {
	private String timestamp;
	private ExerciseDTO exercise;
	private List<ExerciseSetDTO> sets;
	private Boolean failure;
}
