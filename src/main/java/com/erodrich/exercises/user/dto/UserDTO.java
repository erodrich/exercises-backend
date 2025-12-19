package com.erodrich.exercises.user.dto;

import com.erodrich.exercises.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String id;
	private String username;
	private String email;
	private Role role;
}
