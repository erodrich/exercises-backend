package com.erodrich.exercises.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String secret = "exercises-secret-key-change-this-in-production-must-be-at-least-256-bits-long";
	private long expiration = 86400000; // 24 hours in milliseconds
}
