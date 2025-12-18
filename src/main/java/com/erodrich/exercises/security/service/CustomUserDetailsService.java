package com.erodrich.exercises.security.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Username is actually email in our system
		UserEntity user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
		
		return User.builder()
				.username(user.getEmail()) // Use email as username
				.password(user.getPassword())
				.authorities(new ArrayList<>())
				.build();
	}
	
	/**
	 * Load user by email (for login)
	 */
	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		
		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(new ArrayList<>())
				.build();
	}
}
