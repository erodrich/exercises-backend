package com.erodrich.exercises.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.erodrich.exercises.user.entity.UserEntity;

@DataJpaTest
class UserRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void findByUsername_whenUserExists_shouldReturnUser() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("testuser");
		user.setPassword("password123");
		user.setEmail("test@email.com");
		user.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user);
		entityManager.flush();
		
		// When
		Optional<UserEntity> found = userRepository.findByUsername("testuser");
		
		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getUsername()).isEqualTo("testuser");
		assertThat(found.get().getEmail()).isEqualTo("test@email.com");
	}
	
	@Test
	void findByUsername_whenUserDoesNotExist_shouldReturnEmpty() {
		// When
		Optional<UserEntity> found = userRepository.findByUsername("nonexistent");
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void findByEmail_whenUserExists_shouldReturnUser() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("testuser");
		user.setPassword("password123");
		user.setEmail("test@email.com");
		user.setCreatedAt(LocalDateTime.now());
		entityManager.persist(user);
		entityManager.flush();
		
		// When
		Optional<UserEntity> found = userRepository.findByEmail("test@email.com");
		
		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("test@email.com");
		assertThat(found.get().getUsername()).isEqualTo("testuser");
	}
	
	@Test
	void findByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
		// When
		Optional<UserEntity> found = userRepository.findByEmail("nonexistent@email.com");
		
		// Then
		assertThat(found).isEmpty();
	}
	
	@Test
	void save_shouldPersistUser() {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("newuser");
		user.setPassword("pass123");
		user.setEmail("new@email.com");
		user.setCreatedAt(LocalDateTime.now());
		
		// When
		UserEntity saved = userRepository.save(user);
		
		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getUsername()).isEqualTo("newuser");
	}
}
