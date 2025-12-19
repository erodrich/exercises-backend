package com.erodrich.exercises.exerciselogging.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.user.entity.UserEntity;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ExerciseLogRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ExerciseLogRepository exerciseLogRepository;
	
	@Test
	void findByUserId_whenLogsExist_shouldReturnUserLogs() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("testuser");
		user.setPassword("pass");
		user.setEmail("test@email.com");
		user.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user);

		var muscleGroupEntity = new MuscleGroupEntity();
		muscleGroupEntity.setName("CHEST");
		muscleGroupEntity.setDescription("Chest muscle groups");
		entityManager.persist(muscleGroupEntity);

		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Bench Press");
		exercise.setMuscleGroup(muscleGroupEntity);
		entityManager.persist(exercise);

		ExerciseLogEntity log1 = new ExerciseLogEntity();
		log1.setUser(user);
		log1.setExercise(exercise);
		log1.setDate(LocalDateTime.now());
		log1.setHasFailed(false);
		entityManager.persist(log1);

		ExerciseLogEntity log2 = new ExerciseLogEntity();
		log2.setUser(user);
		log2.setExercise(exercise);
		log2.setDate(LocalDateTime.now());
		log2.setHasFailed(false);
		entityManager.persist(log2);
		

		
		// When
		List<ExerciseLogEntity> found = exerciseLogRepository.findByUserId(user.getId());
		
		// Then
		assertThat(found).hasSize(2);
		assertThat(found).allMatch(log -> log.getUser().getId().equals(user.getId()));
	}
	
	@Test
	void findByUserId_whenNoLogsExist_shouldReturnEmptyList() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("testuser");
		user.setPassword("pass");
		user.setEmail("test@email.com");
		user.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user);
		entityManager.flush();
		
		// When
		List<ExerciseLogEntity> found = exerciseLogRepository.findByUserId(user.getId());
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByUserId_shouldOnlyReturnLogsForSpecificUser() {
		// Given
		UserEntity user1 = new UserEntity();
		user1.setUsername("user1");
		user1.setPassword("pass");
		user1.setEmail("user1@email.com");
		user1.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user1);
		
		UserEntity user2 = new UserEntity();
		user2.setUsername("user2");
		user2.setPassword("pass");
		user2.setEmail("user2@email.com");
		user2.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user2);
		
		MuscleGroupEntity legs = new MuscleGroupEntity(null, "LEGS", "Leg exercises");
		entityManager.persist(legs);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Squat");
		exercise.setMuscleGroup(legs);
		entityManager.persist(exercise);
		
		ExerciseLogEntity log1 = new ExerciseLogEntity();
		log1.setUser(user1);
		log1.setExercise(exercise);
		log1.setDate(LocalDateTime.now());
		log1.setHasFailed(false);
		entityManager.persist(log1);
		
		ExerciseLogEntity log2 = new ExerciseLogEntity();
		log2.setUser(user2);
		log2.setExercise(exercise);
		log2.setDate(LocalDateTime.now());
		log2.setHasFailed(false);
		entityManager.persist(log2);
		
		entityManager.flush();
		
		// When
		List<ExerciseLogEntity> user1Logs = exerciseLogRepository.findByUserId(user1.getId());
		
		// Then
		assertThat(user1Logs).hasSize(1);
		assertThat(user1Logs.get(0).getUser().getId()).isEqualTo(user1.getId());
	}
	
	@Test
	void save_shouldPersistExerciseLog() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("testuser");
		user.setPassword("pass");
		user.setEmail("test@email.com");
		user.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user);
		
		MuscleGroupEntity back = new MuscleGroupEntity(null, "BACK", "Back exercises");
		entityManager.persist(back);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setName("Pull Up");
		exercise.setMuscleGroup(back);
		entityManager.persist(exercise);
		
		ExerciseLogEntity log = new ExerciseLogEntity();
		log.setUser(user);
		log.setExercise(exercise);
		log.setDate(LocalDateTime.now());
		log.setHasFailed(true);
		
		// When
		ExerciseLogEntity saved = exerciseLogRepository.save(log);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.isHasFailed()).isTrue();
	}
}
