package com.erodrich.exercises.workoutplan.dto;

import java.util.List;

import com.erodrich.exercises.workoutplan.entity.DurationUnitEnum;

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
public class WorkoutPlanDTO {

	private Long id;
	private String name;
	private Integer duration;
	private DurationUnitEnum durationUnit;
	private boolean isActive;
	private List<WorkoutDayDTO> workoutDayDTOList;
}
