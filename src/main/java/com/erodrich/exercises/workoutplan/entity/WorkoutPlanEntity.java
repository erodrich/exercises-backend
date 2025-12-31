package com.erodrich.exercises.workoutplan.entity;

import java.util.List;

import com.erodrich.exercises.user.entity.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workout_plans")
public class WorkoutPlanEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private Integer duration;
	private DurationUnitEnum durationUnit;
	private boolean isActive;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "workout_plan_id")
	private List<WorkoutDayEntity> workoutDayEntityList;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
}
