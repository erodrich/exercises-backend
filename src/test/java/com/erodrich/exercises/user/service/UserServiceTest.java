package com.erodrich.exercises.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.user.dto.LoginRequest;
import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.mapper.UserMapper;
import com.erodrich.exercises.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserMapper userMapper;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	void register_withValidRequest_shouldReturnUserDTO() {
		// Given
		RegisterRequest request = new RegisterRequest("testuser", "password", "test@email.com");
		
		UserEntity entity = new UserEntity();
		entity.setUsername("testuser");
		entity.setPassword("password");
		entity.setEmail("test@email.com");
		
		UserEntity savedEntity = new UserEntity();
		savedEntity.setId(1L);
		savedEntity.setUsername("testuser");
		savedEntity.setPassword("password");
		savedEntity.setEmail("test@email.com");
		savedEntity.setCreatedAt(LocalDateTime.now());
		
		UserDTO expectedDTO = new UserDTO(1L, "testuser", "test@email.com", "12/16/2025 10:30:00");
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		when(userRepository.findAll()).thenReturn(Arrays.asList());
		when(userMapper.toEntity(request)).thenReturn(entity);
		when(userRepository.save(entity)).thenReturn(savedEntity);
		when(userMapper.toDTO(savedEntity)).thenReturn(expectedDTO);
		
		// When
		UserDTO result = userService.register(request);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("testuser");
		verify(userRepository).save(entity);
	}
	
	@Test
	void register_withExistingUsername_shouldThrowException() {
		// Given
		RegisterRequest request = new RegisterRequest("existinguser", "password", "test@email.com");
		
		UserEntity existingUser = new UserEntity();
		existingUser.setUsername("existinguser");
		
		when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));
		
		// When/Then
		assertThatThrownBy(() -> userService.register(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Username already exists");
	}
	
	@Test
	void register_withExistingEmail_shouldThrowException() {
		// Given
		RegisterRequest request = new RegisterRequest("newuser", "password", "existing@email.com");
		
		UserEntity existingUser = new UserEntity();
		existingUser.setEmail("existing@email.com");
		
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
		when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser));
		
		// When/Then
		assertThatThrownBy(() -> userService.register(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Email already exists");
	}
	
	@Test
	void login_withValidCredentials_shouldReturnUserDTO() {
		// Given
		LoginRequest request = new LoginRequest("testuser", "password");
		
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername("testuser");
		user.setPassword("password");
		
		UserDTO expectedDTO = new UserDTO(1L, "testuser", "test@email.com", "12/16/2025 10:30:00");
		
		when(userRepository.findByUsernameAndPassword("testuser", "password")).thenReturn(Optional.of(user));
		when(userMapper.toDTO(user)).thenReturn(expectedDTO);
		
		// When
		UserDTO result = userService.login(request);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("testuser");
	}
	
	@Test
	void login_withInvalidCredentials_shouldThrowException() {
		// Given
		LoginRequest request = new LoginRequest("testuser", "wrongpassword");
		
		when(userRepository.findByUsernameAndPassword("testuser", "wrongpassword")).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> userService.login(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid username or password");
	}
	
	@Test
	void getUserByUsername_whenUserExists_shouldReturnUserDTO() {
		// Given
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername("testuser");
		
		UserDTO expectedDTO = new UserDTO(1L, "testuser", "test@email.com", "12/16/2025 10:30:00");
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(userMapper.toDTO(user)).thenReturn(expectedDTO);
		
		// When
		UserDTO result = userService.getUserByUsername("testuser");
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("testuser");
	}
	
	@Test
	void getUserByUsername_whenUserDoesNotExist_shouldThrowException() {
		// Given
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> userService.getUserByUsername("nonexistent"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("User not found");
	}
}
