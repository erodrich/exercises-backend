package com.erodrich.exercises.exercise.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;

@DataJpaTest
class ExerciseRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ExerciseRepository exerciseRepository;
	
	@Autowired
	private MuscleGroupRepository muscleGroupRepository;
	
	@Test
	void findByNameAndMuscleGroup_whenExerciseExists_shouldReturnExercise() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(null, "CHEST", "Chest exercises");
		chest = entityManager.persist(chest);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setMuscleGroup(chest);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndMuscleGroup("Bench Press", chest);
		
		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Bench Press");
		assertThat(found.get().getMuscleGroup().getName()).isEqualTo("CHEST");
	}
	
	@Test
	void findByNameAndMuscleGroup_whenNameDoesNotMatch_shouldReturnEmpty() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(null, "CHEST", "Chest exercises");
		chest = entityManager.persist(chest);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setMuscleGroup(chest);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndMuscleGroup("Squat", chest);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByNameAndMuscleGroup_whenGroupDoesNotMatch_shouldReturnEmpty() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(null, "CHEST", "Chest exercises");
		chest = entityManager.persist(chest);
		MuscleGroupEntity legs = new MuscleGroupEntity(null, "LEGS", "Leg exercises");
		legs = entityManager.persist(legs);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setMuscleGroup(chest);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndMuscleGroup("Bench Press", legs);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByNameAndMuscleGroup_whenNothingExists_shouldReturnEmpty() {
		// Given
		MuscleGroupEntity back = new MuscleGroupEntity(null, "BACK", "Back exercises");
		back = entityManager.persist(back);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndMuscleGroup("Nonexistent", back);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void save_shouldPersistExercise() {
		// Given
		MuscleGroupEntity back = new MuscleGroupEntity(null, "BACK", "Back exercises");
		back = entityManager.persist(back);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Deadlift");
		exercise.setMuscleGroup(back);
		
		// When
		ExerciseEntity saved = exerciseRepository.save(exercise);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("Deadlift");
		assertThat(saved.getMuscleGroup().getName()).isEqualTo("BACK");
	}
	
	@Test
	void findByNameAndMuscleGroup_shouldBeCaseSensitive() {
		// Given
		MuscleGroupEntity chest = new MuscleGroupEntity(null, "CHEST", "Chest exercises");
		chest = entityManager.persist(chest);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setMuscleGroup(chest);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndMuscleGroup("bench press", chest);
		
		// Then
		assertThat(found).isEmpty();
	}
}
