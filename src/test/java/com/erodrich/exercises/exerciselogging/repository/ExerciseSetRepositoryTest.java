package com.erodrich.exercises.exerciselogging.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;

@DataJpaTest
class ExerciseSetRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ExerciseSetRepository exerciseSetRepository;
	
	@Test
	void save_shouldPersistExerciseSet() {
		// Given
		ExerciseSetEntity set = new ExerciseSetEntity();
		set.setWeight(100.0);
		set.setReps(10);
		
		// When
		ExerciseSetEntity saved = exerciseSetRepository.save(set);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getWeight()).isEqualTo(100.0);
		assertThat(saved.getReps()).isEqualTo(10);
	}
	
	@Test
	void findById_whenSetExists_shouldReturnSet() {
		// Given
		ExerciseSetEntity set = new ExerciseSetEntity();
		set.setWeight(50.0);
		set.setReps(12);
		entityManager.persist(set);
		entityManager.flush();
		
		// When
		Optional<ExerciseSetEntity> found = exerciseSetRepository.findById(set.getId());
		
		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getWeight()).isEqualTo(50.0);
		assertThat(found.get().getReps()).isEqualTo(12);
	}
	
	@Test
	void findById_whenSetDoesNotExist_shouldReturnEmpty() {
		// When
		Optional<ExerciseSetEntity> found = exerciseSetRepository.findById(999L);
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void delete_shouldRemoveSet() {
		// Given
		ExerciseSetEntity set = new ExerciseSetEntity();
		set.setWeight(75.0);
		set.setReps(8);
		entityManager.persist(set);
		entityManager.flush();
		Long setId = set.getId();
		
		// When
		exerciseSetRepository.deleteById(setId);
		
		// Then
		Optional<ExerciseSetEntity> found = exerciseSetRepository.findById(setId);
		assertThat(found).isEmpty();
	}
	
	@Test
	void save_withZeroWeight_shouldPersist() {
		// Given (bodyweight exercise)
		ExerciseSetEntity set = new ExerciseSetEntity();
		set.setWeight(0.0);
		set.setReps(15);
		
		// When
		ExerciseSetEntity saved = exerciseSetRepository.save(set);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getWeight()).isEqualTo(0.0);
		assertThat(saved.getReps()).isEqualTo(15);
	}
}
