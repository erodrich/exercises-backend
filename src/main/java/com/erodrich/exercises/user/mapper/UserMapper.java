package com.erodrich.exercises.user.mapper;

import org.springframework.stereotype.Component;

import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.entity.UserEntity;

@Component
public class UserMapper {
	
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
		dto.setId(entity.getId().toString());
		dto.setUsername(entity.getUsername());
		dto.setEmail(entity.getEmail());
		
		return dto;
	}
}
