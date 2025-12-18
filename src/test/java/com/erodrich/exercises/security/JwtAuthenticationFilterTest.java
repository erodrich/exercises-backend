package com.erodrich.exercises.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.erodrich.exercises.security.jwt.JwtAuthenticationFilter;
import com.erodrich.exercises.security.jwt.JwtTokenProvider;
import com.erodrich.exercises.security.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
	
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	
	@Mock
	private CustomUserDetailsService customUserDetailsService;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private FilterChain filterChain;
	
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@BeforeEach
	void setUp() {
		jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
		SecurityContextHolder.clearContext();
	}
	
	@Test
	void doFilterInternal_withValidToken_shouldSetAuthentication() throws ServletException, IOException {
		// Given
		String token = "valid.jwt.token";
		String email = "test@email.com";
		UserDetails userDetails = User.builder()
			.username(email)
			.password("password")
			.authorities(Collections.emptyList())
			.build();
		
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtTokenProvider.validateToken(token)).thenReturn(true);
		when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(email);
		when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
		assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
		assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	void doFilterInternal_withInvalidToken_shouldNotSetAuthentication() throws ServletException, IOException {
		// Given
		String token = "invalid.jwt.token";
		
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtTokenProvider.validateToken(token)).thenReturn(false);
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(customUserDetailsService, never()).loadUserByUsername(anyString());
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	void doFilterInternal_withNoAuthorizationHeader_shouldNotSetAuthentication() throws ServletException, IOException {
		// Given
		when(request.getHeader("Authorization")).thenReturn(null);
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(jwtTokenProvider, never()).validateToken(anyString());
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	void doFilterInternal_withInvalidAuthorizationHeader_shouldNotSetAuthentication() throws ServletException, IOException {
		// Given
		when(request.getHeader("Authorization")).thenReturn("InvalidHeader token");
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(jwtTokenProvider, never()).validateToken(anyString());
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	void doFilterInternal_withEmptyBearerToken_shouldNotSetAuthentication() throws ServletException, IOException {
		// Given
		when(request.getHeader("Authorization")).thenReturn("Bearer ");
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(jwtTokenProvider, never()).validateToken(anyString());
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	void doFilterInternal_whenExceptionOccurs_shouldContinueFilterChain() throws ServletException, IOException {
		// Given
		String token = "valid.jwt.token";
		
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtTokenProvider.validateToken(token)).thenReturn(true);
		when(jwtTokenProvider.getUsernameFromToken(token)).thenThrow(new RuntimeException("JWT parsing error"));
		
		// When
		jwtAuthenticationFilter.doFilter(request, response, filterChain);
		
		// Then
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(filterChain).doFilter(request, response);
	}
}
