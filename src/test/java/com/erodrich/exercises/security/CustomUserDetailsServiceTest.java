package com.erodrich.exercises.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.erodrich.exercises.security.service.CustomUserDetailsService;
import com.erodrich.exercises.user.entity.Role;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;
	
	@Test
	void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
		// Given
		String email = "test@email.com";
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail(email);
		user.setPassword("hashedPassword");
		user.setRole(Role.USER);
		user.setCreatedAt(LocalDateTime.now());
		
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		
		// When
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
		
		// Then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUsername()).isEqualTo(email);
		assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
		assertThat(userDetails.getAuthorities()).hasSize(1);
		assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
		assertThat(userDetails.isEnabled()).isTrue();
		assertThat(userDetails.isAccountNonExpired()).isTrue();
		assertThat(userDetails.isAccountNonLocked()).isTrue();
		assertThat(userDetails.isCredentialsNonExpired()).isTrue();
	}
	
	@Test
	void loadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
		// Given
		String email = "nonexistent@email.com";
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage("User not found with email: " + email);
	}
	
	@Test
	void loadUserByUsername_withDifferentUsers_shouldReturnCorrectDetails() {
		// Given
		String email1 = "user1@email.com";
		String email2 = "user2@email.com";
		
		UserEntity user1 = new UserEntity();
		user1.setEmail(email1);
		user1.setPassword("password1");
		user1.setRole(Role.USER);
		
		UserEntity user2 = new UserEntity();
		user2.setEmail(email2);
		user2.setPassword("password2");
		user2.setRole(Role.ADMIN);
		
		when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
		when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));
		
		// When
		UserDetails userDetails1 = customUserDetailsService.loadUserByUsername(email1);
		UserDetails userDetails2 = customUserDetailsService.loadUserByUsername(email2);
		
		// Then
		assertThat(userDetails1.getUsername()).isEqualTo(email1);
		assertThat(userDetails1.getPassword()).isEqualTo("password1");
		assertThat(userDetails2.getUsername()).isEqualTo(email2);
		assertThat(userDetails2.getPassword()).isEqualTo("password2");
	}
}
