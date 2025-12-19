package com.erodrich.exercises.security.service;

import java.util.Collections;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
	public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
		// Username is actually email in our system
		UserEntity user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
		
		// Add role as authority
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
		
		return User.builder()
				.username(user.getEmail()) // Use email as username
				.password(user.getPassword())
				.authorities(Collections.singletonList(authority))
				.build();
	}
	
	/**
	 * Load user by email (for login)
	 */
	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		
		// Add role as authority
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
		
		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(Collections.singletonList(authority))
				.build();
	}
}
