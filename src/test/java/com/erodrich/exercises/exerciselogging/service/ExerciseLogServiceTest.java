package com.erodrich.exercises.exerciselogging.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erodrich.exercises.exercise.entity.ExerciseEntity;
import com.erodrich.exercises.exercise.entity.MuscleGroup;
import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.exerciselogging.dto.ExerciseDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseSetDTO;
import com.erodrich.exercises.exerciselogging.entity.ExerciseLogEntity;
import com.erodrich.exercises.exerciselogging.entity.ExerciseSetEntity;
import com.erodrich.exercises.exerciselogging.mapper.ExerciseLogMapper;
import com.erodrich.exercises.exerciselogging.repository.ExerciseLogRepository;
import com.erodrich.exercises.exerciselogging.repository.ExerciseSetRepository;
import com.erodrich.exercises.user.entity.UserEntity;
import com.erodrich.exercises.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseLogServiceTest {
	
	@Mock
	private ExerciseLogRepository exerciseLogRepository;
	
	@Mock
	private ExerciseRepository exerciseRepository;
	
	@Mock
	private ExerciseSetRepository exerciseSetRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ExerciseLogMapper mapper;
	
	@InjectMocks
	private ExerciseLogService exerciseLogService;
	
	@Test
	void saveLogs_withValidData_shouldSaveAndReturnLogs() {
		// Given
		Long userId = 1L;
		
		UserEntity user = new UserEntity();
		user.setId(userId);
		user.setUsername("testuser");
		
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Bench Press", "CHEST");
		ExerciseSetDTO setDTO = new ExerciseSetDTO(100.0, 10);
		ExerciseLogDTO logDTO = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, Arrays.asList(setDTO), false);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setId(1L);
		exercise.setName("Bench Press");
		exercise.setGroup(MuscleGroup.CHEST);
		
		ExerciseLogEntity logEntity = new ExerciseLogEntity();
		logEntity.setDate(LocalDateTime.now());
		logEntity.setExercise(exercise);
		
		ExerciseSetEntity setEntity = new ExerciseSetEntity();
		setEntity.setId(1L);
		setEntity.setWeight(100.0);
		setEntity.setReps(10);
		
		ExerciseLogEntity savedLogEntity = new ExerciseLogEntity();
		savedLogEntity.setId(1L);
		savedLogEntity.setUser(user);
		savedLogEntity.setExercise(exercise);
		savedLogEntity.setSets(new HashSet<>(Arrays.asList(setEntity)));
		
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(mapper.toEntity(logDTO)).thenReturn(logEntity);
		when(exerciseRepository.findByNameAndGroup("Bench Press", MuscleGroup.CHEST))
			.thenReturn(Optional.of(exercise));
		when(exerciseSetRepository.save(any(ExerciseSetEntity.class))).thenReturn(setEntity);
		when(exerciseLogRepository.saveAll(any())).thenReturn(Arrays.asList(savedLogEntity));
		when(mapper.toDTO(savedLogEntity)).thenReturn(logDTO);
		
		// When
		List<ExerciseLogDTO> result = exerciseLogService.saveLogs(userId, Arrays.asList(logDTO));
		
		// Then
		assertThat(result).hasSize(1);
		verify(exerciseLogRepository).saveAll(any());
	}
	
	@Test
	void saveLogs_withInvalidUserId_shouldThrowException() {
		// Given
		Long userId = 999L;
		ExerciseLogDTO logDTO = new ExerciseLogDTO();
		
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		// When/Then
		assertThatThrownBy(() -> exerciseLogService.saveLogs(userId, Arrays.asList(logDTO)))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("User not found");
	}
	
	@Test
	void saveLogs_withNewExercise_shouldCreateExercise() {
		// Given
		Long userId = 1L;
		
		UserEntity user = new UserEntity();
		user.setId(userId);
		
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "New Exercise", "LEGS");
		ExerciseLogDTO logDTO = new ExerciseLogDTO("12/16/2025 10:30:00", exerciseDTO, Arrays.asList(), false);
		
		ExerciseLogEntity logEntity = new ExerciseLogEntity();
		
		ExerciseEntity newExercise = new ExerciseEntity();
		newExercise.setId(1L);
		newExercise.setName("New Exercise");
		newExercise.setGroup(MuscleGroup.LEGS);
		
		ExerciseLogEntity savedLogEntity = new ExerciseLogEntity();
		savedLogEntity.setId(1L);
		
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(mapper.toEntity(logDTO)).thenReturn(logEntity);
		when(exerciseRepository.findByNameAndGroup("New Exercise", MuscleGroup.LEGS))
			.thenReturn(Optional.empty());
		when(exerciseRepository.save(any(ExerciseEntity.class))).thenReturn(newExercise);
		when(exerciseLogRepository.saveAll(any())).thenReturn(Arrays.asList(savedLogEntity));
		when(mapper.toDTO(savedLogEntity)).thenReturn(logDTO);
		
		// When
		List<ExerciseLogDTO> result = exerciseLogService.saveLogs(userId, Arrays.asList(logDTO));
		
		// Then
		assertThat(result).isNotEmpty();
		verify(exerciseRepository).save(any(ExerciseEntity.class));
	}
	
	@Test
	void getAllLogs_withValidUserId_shouldReturnUserLogs() {
		// Given
		Long userId = 1L;
		
		ExerciseLogEntity logEntity = new ExerciseLogEntity();
		logEntity.setId(1L);
		
		ExerciseLogDTO logDTO = new ExerciseLogDTO();
		
		when(exerciseLogRepository.findByUserId(userId)).thenReturn(Arrays.asList(logEntity));
		when(mapper.toDTO(logEntity)).thenReturn(logDTO);
		
		// When
		List<ExerciseLogDTO> result = exerciseLogService.getAllLogs(userId);
		
		// Then
		assertThat(result).hasSize(1);
		verify(exerciseLogRepository).findByUserId(userId);
	}
	
	@Test
	void getAllLogs_whenNoLogs_shouldReturnEmptyList() {
		// Given
		Long userId = 1L;
		
		when(exerciseLogRepository.findByUserId(userId)).thenReturn(Arrays.asList());
		
		// When
		List<ExerciseLogDTO> result = exerciseLogService.getAllLogs(userId);
		
		// Then
		assertThat(result).isEmpty();
	}
	
	@Test
	void saveLogs_shouldPersistAllSets() {
		// Given
		Long userId = 1L;
		
		UserEntity user = new UserEntity();
		user.setId(userId);
		
		ExerciseDTO exerciseDTO = new ExerciseDTO(null, "Squat", "LEGS");
		ExerciseSetDTO set1 = new ExerciseSetDTO(100.0, 10);
		ExerciseSetDTO set2 = new ExerciseSetDTO(100.0, 8);
		ExerciseSetDTO set3 = new ExerciseSetDTO(100.0, 6);
		ExerciseLogDTO logDTO = new ExerciseLogDTO(
			"12/16/2025 10:30:00", 
			exerciseDTO, 
			Arrays.asList(set1, set2, set3), 
			false
		);
		
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setId(1L);
		
		ExerciseLogEntity logEntity = new ExerciseLogEntity();
		
		ExerciseSetEntity setEntity = new ExerciseSetEntity();
		setEntity.setId(1L);
		
		ExerciseLogEntity savedLogEntity = new ExerciseLogEntity();
		savedLogEntity.setId(1L);
		
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(mapper.toEntity(logDTO)).thenReturn(logEntity);
		when(exerciseRepository.findByNameAndGroup("Squat", MuscleGroup.LEGS))
			.thenReturn(Optional.of(exercise));
		when(exerciseSetRepository.save(any(ExerciseSetEntity.class))).thenReturn(setEntity);
		when(exerciseLogRepository.saveAll(any())).thenReturn(Arrays.asList(savedLogEntity));
		when(mapper.toDTO(savedLogEntity)).thenReturn(logDTO);
		
		// When
		exerciseLogService.saveLogs(userId, Arrays.asList(logDTO));
		
		// Then
		verify(exerciseSetRepository, times(3)).save(any(ExerciseSetEntity.class));
	}
}
