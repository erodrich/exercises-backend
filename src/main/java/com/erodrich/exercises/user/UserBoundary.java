package com.erodrich.exercises.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erodrich.exercises.user.dto.AuthResponse;
import com.erodrich.exercises.user.dto.LoginRequest;
import com.erodrich.exercises.user.dto.RegisterRequest;
import com.erodrich.exercises.user.dto.UserDTO;
import com.erodrich.exercises.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/users")
@Validated
@Slf4j
public class UserBoundary {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		try {
			AuthResponse response = userService.register(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			log.error("Error: [{}] - Request: {}", e.getMessage(), request);
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		try {
			AuthResponse response = userService.login(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.error("Error: [{}] - Request: {}", e.getMessage(), request);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/{username}")
	public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
		try {
			UserDTO user = userService.getUserByUsername(username);
			return ResponseEntity.ok(user);
		} catch (IllegalArgumentException e) {
			log.error("Error: [{}] - Request: {}", e.getMessage(), username);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
