package com.erodrich.exercises.exercise.entity;

import java.util.List;

import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class ExerciseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	@Column(name = "muscle_group")
	private MuscleGroup group;
	@OneToMany
	@JoinColumn(name = "exercise_id")
	private List<ExerciseLogEntity> logs;


}