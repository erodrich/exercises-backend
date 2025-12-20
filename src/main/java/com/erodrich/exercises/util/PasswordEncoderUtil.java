package com.erodrich.exercises.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Util to encode passwords for testing purposes
 */
public class PasswordEncoderUtil {

	public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static void main(String[] args) {
		//To encode a password edit encodePasswords() method and run as java
		System.out.println("Encoded passwords: " + encodePassword());

		//To validate a password edit isPasswordValid() method and run as java
		System.out.println("Is password valid: " + isPasswordValid("Perito123$dmin", "$2a$10$LK6UK4KA0qJMpBx4XvL0yOTiLKqM7mWmyEISwf7rAcKADXLI85FJi"));
	}

	private static boolean isPasswordValid(String rawPassword, String hash) {
		return passwordEncoder.matches(rawPassword, hash);
	}

	private static String encodePassword() {
		var rawPassword = "Perito123$dmin";
		return passwordEncoder.encode(rawPassword);
	}
}
