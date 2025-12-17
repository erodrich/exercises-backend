package com.erodrich.exercises.exerciselogging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
	private Long id;
	private String name;
	private String group;
}
