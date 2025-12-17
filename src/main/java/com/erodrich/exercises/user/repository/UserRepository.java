package com.erodrich.exercises.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erodrich.exercises.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsername(String username);
	
	Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
