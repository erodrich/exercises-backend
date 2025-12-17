package com.erodrich.exercises.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
	public UserDTO register(RegisterRequest request) {
		// Check if username already exists
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		
		// Check if email already exists
		Optional<UserEntity> existingUserByEmail = userRepository.findAll().stream()
				.filter(u -> u.getEmail().equals(request.getEmail()))
				.findFirst();
		
		if (existingUserByEmail.isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}
		
		UserEntity entity = userMapper.toEntity(request);
		UserEntity savedEntity = userRepository.save(entity);
		
		return userMapper.toDTO(savedEntity);
	}
	
	@Transactional(readOnly = true)
	public UserDTO login(LoginRequest request) {
		UserEntity user = userRepository.findByUsernameAndPassword(
				request.getUsername(), 
				request.getPassword())
			.orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
		
		return userMapper.toDTO(user);
	}
	
	@Transactional(readOnly = true)
	public UserDTO getUserByUsername(String username) {
		UserEntity user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		return userMapper.toDTO(user);
	}
}
