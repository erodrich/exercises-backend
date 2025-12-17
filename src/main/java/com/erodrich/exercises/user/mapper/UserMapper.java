package com.erodrich.exercises.user.mapper;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.entity.UserEntity;

@Component
public class UserMapper {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	
	public UserEntity toEntity(RegisterRequest request) {
		if (request == null) {
			return null;
		}
		
		UserEntity entity = new UserEntity();
		entity.setUsername(request.getUsername());
		entity.setPassword(request.getPassword());
		entity.setEmail(request.getEmail());
		
		return entity;
	}
	
	public UserDTO toDTO(UserEntity entity) {
		if (entity == null) {
			return null;
		}
		
		UserDTO dto = new UserDTO();
		dto.setId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setEmail(entity.getEmail());
		dto.setCreatedAt(entity.getCreatedAt() != null ? 
				entity.getCreatedAt().format(FORMATTER) : null);
		
		return dto;
	}
}
