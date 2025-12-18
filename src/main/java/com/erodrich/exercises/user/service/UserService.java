package com.erodrich.exercises.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erodrich.exercises.security.jwt.JwtTokenProvider;
import com.erodrich.exercises.user.dto.AuthResponse;
import com.erodrich.exercises.user.dto.LoginRequest;
import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.mapper.UserMapper;
import com.erodrich.exercises.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		// Check if username already exists
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		
		// Check if email already exists
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}
		
		// Create entity and hash password
		UserEntity entity = userMapper.toEntity(request);
		entity.setPassword(passwordEncoder.encode(request.getPassword()));
		
		// Save user
		UserEntity savedEntity = userRepository.save(entity);
		
		// Generate JWT token using email (email is our username)
		String token = jwtTokenProvider.generateToken(savedEntity.getEmail());
		
		// Return response with user and token
		UserDTO userDTO = userMapper.toDTO(savedEntity);
		return new AuthResponse(userDTO, token);
	}
	
	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		// Find user by email
		UserEntity user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
		
		// Verify password
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}
		
		// Generate JWT token using email (email is our username)
		String token = jwtTokenProvider.generateToken(user.getEmail());
		
		// Return response with user and token
		UserDTO userDTO = userMapper.toDTO(user);
		return new AuthResponse(userDTO, token);
	}
	
	@Transactional(readOnly = true)
	public UserDTO getUserByUsername(String username) {
		UserEntity user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		return userMapper.toDTO(user);
	}
}
