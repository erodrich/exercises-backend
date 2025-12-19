package com.erodrich.exercises.user.entity;

/**
 * User roles for authorization
 */
public enum Role {
	/**
	 * Regular user - can log exercises and view their own data
	 */
	USER,
	
	/**
	 * Administrator - can manage master data (exercises, users)
	 */
	ADMIN
}
