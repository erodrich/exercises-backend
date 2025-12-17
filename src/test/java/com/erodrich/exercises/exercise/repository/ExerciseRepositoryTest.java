package com.erodrich.exercises.exercise.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;

@DataJpaTest
class ExerciseRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ExerciseRepository exerciseRepository;
	
	@Test
	void findByNameAndGroup_whenExerciseExists_shouldReturnExercise() {
		// Given
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setGroup(MuscleGroup.CHEST);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndGroup("Bench Press", MuscleGroup.CHEST);
		
		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Bench Press");
		assertThat(found.get().getGroup()).isEqualTo(MuscleGroup.CHEST);
	}
	
	@Test
	void findByNameAndGroup_whenNameDoesNotMatch_shouldReturnEmpty() {
		// Given
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setGroup(MuscleGroup.CHEST);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndGroup("Squat", MuscleGroup.CHEST);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByNameAndGroup_whenGroupDoesNotMatch_shouldReturnEmpty() {
		// Given
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setGroup(MuscleGroup.CHEST);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndGroup("Bench Press", MuscleGroup.LEGS);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByNameAndGroup_whenNothingExists_shouldReturnEmpty() {
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndGroup("Nonexistent", MuscleGroup.BACK);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void save_shouldPersistExercise() {
		// Given
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Deadlift");
		exercise.setGroup(MuscleGroup.BACK);
		
		// When
		ExerciseEntity saved = exerciseRepository.save(exercise);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("Deadlift");
		assertThat(saved.getGroup()).isEqualTo(MuscleGroup.BACK);
	}
	
	@Test
	void findByNameAndGroup_shouldBeCaseSensitive() {
		// Given
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setGroup(MuscleGroup.CHEST);
		entityManager.persist(exercise);
		entityManager.flush();
		entityManager.detach(exercise);
		
		// When
		Optional<ExerciseEntity> found = exerciseRepository.findByNameAndGroup("bench press", MuscleGroup.CHEST);
		
		// Then
		assertThat(found).isEmpty();
	}
}
