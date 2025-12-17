package com.erodrich.exercises.exerciselogging.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class ExerciseLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "exercise_log_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private ExerciseEntity exercise;
	@ManyToMany
	@JoinTable(
			name = "exercise_log_sets",
			joinColumns = @JoinColumn(name = "exercise_log_id"),
			inverseJoinColumns = @JoinColumn(name = "exercise_set_id"))
	private Set<ExerciseSetEntity> sets;
	private boolean hasFailed;
	private LocalDateTime date;
}
