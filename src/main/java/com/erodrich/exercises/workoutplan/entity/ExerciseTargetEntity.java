package com.erodrich.exercises.workoutplan.entity;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exercise_target")
public class ExerciseTargetEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private ExerciseEntity exercise;
	private Integer sets;
	private Integer minReps;
	private Integer maxReps;
	@ManyToOne
	@JoinColumn(name = "workout_day_id")
	private WorkoutDayEntity workoutDay;
}
