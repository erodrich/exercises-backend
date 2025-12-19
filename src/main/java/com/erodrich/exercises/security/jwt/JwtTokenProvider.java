package com.erodrich.exercises.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	
	private final JwtProperties jwtProperties;
	
	/**
	 * Generate secret key from configured secret
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
	}
	
	/**
	 * Generate JWT token for user
	 */
	public String generateToken(UserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());
		
		// Extract role from authorities
		String role = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.findFirst()
				.orElse("ROLE_USER");
		
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}
	
	/**
	 * Generate JWT token from username and role
	 */
	public String generateToken(String username, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());
		
		return Jwts.builder()
				.subject(username)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}
	
	/**
	 * Get username from token
	 */
	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return claims.getSubject();
	}
	
	/**
	 * Get email from token (alias for getUsernameFromToken since we use email as username)
	 */
	public String getEmailFromToken(String token) {
		return getUsernameFromToken(token);
	}
	
	/**
	 * Get role from token
	 */
	public String getRoleFromToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return claims.get("role", String.class);
	}
	
	/**
	 * Validate JWT token
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
