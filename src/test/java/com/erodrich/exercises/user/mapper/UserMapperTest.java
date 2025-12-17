package com.erodrich.exercises.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.entity.UserEntity;

class UserMapperTest {
	
	private UserMapper userMapper;
	
	@BeforeEach
	void setUp() {
		userMapper = new UserMapper();
	}
	
	@Test
	void toEntity_withValidRequest_shouldMapCorrectly() {
		// Given
		RegisterRequest request = new RegisterRequest("testuser", "password123", "test@email.com");
		
		// When
		UserEntity entity = userMapper.toEntity(request);
		
		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getUsername()).isEqualTo("testuser");
		assertThat(entity.getPassword()).isEqualTo("password123");
		assertThat(entity.getEmail()).isEqualTo("test@email.com");
		assertThat(entity.getId()).isNull();
		assertThat(entity.getCreatedAt()).isNull();
	}
	
	@Test
	void toEntity_withNullRequest_shouldReturnNull() {
		// When
		UserEntity entity = userMapper.toEntity(null);
		
		// Then
		assertThat(entity).isNull();
	}
	
	@Test
	void toDTO_withValidEntity_shouldMapCorrectly() {
		// Given
		UserEntity entity = new UserEntity();
		entity.setId(1L);
		entity.setUsername("testuser");
		entity.setEmail("test@email.com");
		entity.setPassword("password123");
		entity.setCreatedAt(LocalDateTime.of(2025, 12, 16, 10, 30, 0));
		
		// When
		UserDTO dto = userMapper.toDTO(entity);
		
		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getUsername()).isEqualTo("testuser");
		assertThat(dto.getEmail()).isEqualTo("test@email.com");
		assertThat(dto.getCreatedAt()).isEqualTo("12/16/2025 10:30:00");
	}
	
	@Test
	void toDTO_withNullEntity_shouldReturnNull() {
		// When
		UserDTO dto = userMapper.toDTO(null);
		
		// Then
		assertThat(dto).isNull();
	}
	
	@Test
	void toDTO_withNullCreatedAt_shouldHandleGracefully() {
		// Given
		UserEntity entity = new UserEntity();
		entity.setId(1L);
		entity.setUsername("testuser");
		entity.setEmail("test@email.com");
		entity.setCreatedAt(null);
		
		// When
		UserDTO dto = userMapper.toDTO(entity);
		
		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getCreatedAt()).isNull();
	}
}
