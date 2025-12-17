package com.erodrich.exercises.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.user.dto.LoginRequest;
import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserBoundary {
	
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
		try {
			UserDTO user = userService.register(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
		try {
			UserDTO user = userService.login(request);
			return ResponseEntity.ok(user);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
		try {
			UserDTO user = userService.getUserByUsername(username);
			return ResponseEntity.ok(user);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
