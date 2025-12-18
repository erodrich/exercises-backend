package com.erodrich.exercises.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.erodrich.exercises.security.jwt.JwtProperties;
import com.erodrich.exercises.security.jwt.JwtTokenProvider;

class JwtTokenProviderTest {
	
	private JwtTokenProvider jwtTokenProvider;
	private JwtProperties jwtProperties;
	
	@BeforeEach
	void setUp() {
		jwtProperties = new JwtProperties();
		// Use a valid 256-bit key for HS256
		ReflectionTestUtils.setField(jwtProperties, "secret", "myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm");
		ReflectionTestUtils.setField(jwtProperties, "expiration", 86400000L); // 24 hours
		
		jwtTokenProvider = new JwtTokenProvider(jwtProperties);
	}
	
	@Test
	void generateToken_withValidEmail_shouldReturnToken() {
		// Given
		String email = "test@email.com";
		
		// When
		String token = jwtTokenProvider.generateToken(email);
		
		// Then
		assertThat(token).isNotNull();
		assertThat(token).isNotEmpty();
		assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
	}
	
	@Test
	void getEmailFromToken_withValidToken_shouldReturnEmail() {
		// Given
		String email = "test@email.com";
		String token = jwtTokenProvider.generateToken(email);
		
		// When
		String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
		
		// Then
		assertThat(extractedEmail).isEqualTo(email);
	}
	
	@Test
	void validateToken_withValidToken_shouldReturnTrue() {
		// Given
		String email = "test@email.com";
		String token = jwtTokenProvider.generateToken(email);
		
		// When
		boolean isValid = jwtTokenProvider.validateToken(token);
		
		// Then
		assertThat(isValid).isTrue();
	}
	
	@Test
	void validateToken_withInvalidToken_shouldReturnFalse() {
		// Given
		String invalidToken = "invalid.token.here";
		
		// When
		boolean isValid = jwtTokenProvider.validateToken(invalidToken);
		
		// Then
		assertThat(isValid).isFalse();
	}
	
	@Test
	void validateToken_withExpiredToken_shouldReturnFalse() {
		// Given - Create a token that expires immediately
		JwtProperties shortExpiryProperties = new JwtProperties();
		ReflectionTestUtils.setField(shortExpiryProperties, "secret", "myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm");
		ReflectionTestUtils.setField(shortExpiryProperties, "expiration", -1L); // Already expired
		
		JwtTokenProvider shortExpiryProvider = new JwtTokenProvider(shortExpiryProperties);
		String expiredToken = shortExpiryProvider.generateToken("test@email.com");
		
		// When
		boolean isValid = jwtTokenProvider.validateToken(expiredToken);
		
		// Then
		assertThat(isValid).isFalse();
	}
	
	@Test
	void validateToken_withNullToken_shouldReturnFalse() {
		// When
		boolean isValid = jwtTokenProvider.validateToken(null);
		
		// Then
		assertThat(isValid).isFalse();
	}
	
	@Test
	void validateToken_withEmptyToken_shouldReturnFalse() {
		// When
		boolean isValid = jwtTokenProvider.validateToken("");
		
		// Then
		assertThat(isValid).isFalse();
	}
	
	@Test
	void generateToken_withDifferentEmails_shouldGenerateDifferentTokens() {
		// Given
		String email1 = "user1@email.com";
		String email2 = "user2@email.com";
		
		// When
		String token1 = jwtTokenProvider.generateToken(email1);
		String token2 = jwtTokenProvider.generateToken(email2);
		
		// Then
		assertThat(token1).isNotEqualTo(token2);
		assertThat(jwtTokenProvider.getEmailFromToken(token1)).isEqualTo(email1);
		assertThat(jwtTokenProvider.getEmailFromToken(token2)).isEqualTo(email2);
	}
	
	@Test
	void tokenContainsCorrectClaims() {
		// Given
		String email = "test@email.com";
		String token = jwtTokenProvider.generateToken(email);
		
		// When - Parse token manually to verify claims
		Claims claims = Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
			.build()
			.parseSignedClaims(token)
			.getPayload();
		
		// Then
		assertThat(claims.getSubject()).isEqualTo(email);
		assertThat(claims.getIssuedAt()).isNotNull();
		assertThat(claims.getExpiration()).isNotNull();
		assertThat(claims.getExpiration().getTime()).isGreaterThan(claims.getIssuedAt().getTime());
	}
}
