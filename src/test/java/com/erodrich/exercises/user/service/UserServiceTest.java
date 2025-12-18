package com.erodrich.exercises.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.erodrich.exercises.security.jwt.JwtTokenProvider;
import com.erodrich.exercises.user.dto.AuthResponse;
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
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	void register_withValidRequest_shouldReturnAuthResponse() {
		// Given
		RegisterRequest request = new RegisterRequest("testuser", "test@email.com", "password123");
		
		UserEntity entity = new UserEntity();
		entity.setUsername("testuser");
		entity.setPassword("hashedPassword");
		entity.setEmail("test@email.com");
		
		UserEntity savedEntity = new UserEntity();
		savedEntity.setId(1L);
		savedEntity.setUsername("testuser");
		savedEntity.setPassword("hashedPassword");
		savedEntity.setEmail("test@email.com");
		savedEntity.setCreatedAt(LocalDateTime.now());
		
		UserDTO expectedDTO = new UserDTO("1", "testuser", "test@email.com");
		String expectedToken = "jwt.token.here";
		
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
		when(userMapper.toEntity(request)).thenReturn(entity);
		when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
		when(userMapper.toDTO(savedEntity)).thenReturn(expectedDTO);
		when(jwtTokenProvider.generateToken("test@email.com")).thenReturn(expectedToken);
		
		// When
		AuthResponse result = userService.register(request);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUser()).isNotNull();
		assertThat(result.getUser().getUsername()).isEqualTo("testuser");
		assertThat(result.getToken()).isEqualTo(expectedToken);
		verify(passwordEncoder).encode("password123");
		verify(userRepository).save(any(UserEntity.class));
		verify(jwtTokenProvider).generateToken("test@email.com");
	}
	
	@Test
	void register_withExistingUsername_shouldThrowException() {
		// Given
		RegisterRequest request = new RegisterRequest("existinguser", "test@email.com", "password");
		
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
		RegisterRequest request = new RegisterRequest("newuser", "existing@email.com", "password123");
		
		UserEntity existingUser = new UserEntity();
		existingUser.setEmail("existing@email.com");
		
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
		when(userRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(existingUser));
		
		// When/Then
		assertThatThrownBy(() -> userService.register(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Email already exists");
	}
	
	@Test
	void login_withValidCredentials_shouldReturnAuthResponse() {
		// Given
		LoginRequest request = new LoginRequest("test@email.com", "password123");
		
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail("test@email.com");
		user.setPassword("hashedPassword");
		
		UserDTO expectedDTO = new UserDTO("1", "testuser", "test@email.com");
		String expectedToken = "jwt.token.here";
		
		when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
		when(userMapper.toDTO(user)).thenReturn(expectedDTO);
		when(jwtTokenProvider.generateToken("test@email.com")).thenReturn(expectedToken);
		
		// When
		AuthResponse result = userService.login(request);
		
		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUser()).isNotNull();
		assertThat(result.getUser().getUsername()).isEqualTo("testuser");
		assertThat(result.getToken()).isEqualTo(expectedToken);
		verify(passwordEncoder).matches("password123", "hashedPassword");
		verify(jwtTokenProvider).generateToken("test@email.com");
	}
	
	@Test
	void login_withInvalidEmail_shouldThrowException() {
		// Given
		LoginRequest request = new LoginRequest("nonexistent@email.com", "password123");
		
		when(userRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> userService.login(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid email or password");
	}
	
	@Test
	void login_withInvalidPassword_shouldThrowException() {
		// Given
		LoginRequest request = new LoginRequest("test@email.com", "wrongpassword");
		
		UserEntity user = new UserEntity();
		user.setEmail("test@email.com");
		user.setPassword("hashedPassword");
		
		when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);
		
		// When/Then
		assertThatThrownBy(() -> userService.login(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid email or password");
	}
	
	@Test
	void getUserByUsername_whenUserExists_shouldReturnUserDTO() {
		// Given
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail("test@email.com");
		
		UserDTO expectedDTO = new UserDTO("1", "testuser", "test@email.com");
		
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
