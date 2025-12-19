package com.erodrich.exercises.musclegroup.service;

import com.erodrich.exercises.exercise.repository.ExerciseRepository;
import com.erodrich.exercises.musclegroup.dto.MuscleGroupDTO;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import com.erodrich.exercises.musclegroup.mapper.MuscleGroupMapper;
import com.erodrich.exercises.musclegroup.repository.MuscleGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MuscleGroupService {

    private final MuscleGroupRepository muscleGroupRepository;
    private final MuscleGroupMapper muscleGroupMapper;
    private final ExerciseRepository exerciseRepository;

    public MuscleGroupService(MuscleGroupRepository muscleGroupRepository, 
                             MuscleGroupMapper muscleGroupMapper,
                             ExerciseRepository exerciseRepository) {
        this.muscleGroupRepository = muscleGroupRepository;
        this.muscleGroupMapper = muscleGroupMapper;
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * Get all muscle groups
     *
     * @return list of all muscle groups
     */
    @Transactional(readOnly = true)
    public List<MuscleGroupDTO> getAllMuscleGroups() {
        return muscleGroupRepository.findAll().stream()
                .map(muscleGroupMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a muscle group by ID
     *
     * @param id the muscle group ID
     * @return Optional containing the muscle group if found
     */
    @Transactional(readOnly = true)
    public Optional<MuscleGroupDTO> getMuscleGroupById(Long id) {
        return muscleGroupRepository.findById(id)
                .map(muscleGroupMapper::toDTO);
    }

    /**
     * Get a muscle group by name
     *
     * @param name the muscle group name
     * @return Optional containing the muscle group if found
     */
    @Transactional(readOnly = true)
    public Optional<MuscleGroupDTO> getMuscleGroupByName(String name) {
        return muscleGroupRepository.findByNameIgnoreCase(name)
                .map(muscleGroupMapper::toDTO);
    }

    /**
     * Create a new muscle group
     *
     * @param muscleGroupDTO the muscle group data
     * @return the created muscle group
     * @throws IllegalArgumentException if a muscle group with the same name already exists
     */
    public MuscleGroupDTO createMuscleGroup(MuscleGroupDTO muscleGroupDTO) {
        // Normalize name to uppercase
        String normalizedName = muscleGroupDTO.getName().trim().toUpperCase();
        
        // Check if muscle group with this name already exists
        if (muscleGroupRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Muscle group with name '" + normalizedName + "' already exists");
        }

        MuscleGroupEntity entity = muscleGroupMapper.toEntity(muscleGroupDTO);
        entity.setName(normalizedName);
        entity.setId(null); // Ensure ID is null for new entities
        
        MuscleGroupEntity savedEntity = muscleGroupRepository.save(entity);
        return muscleGroupMapper.toDTO(savedEntity);
    }

    /**
     * Update an existing muscle group
     *
     * @param id the muscle group ID
     * @param muscleGroupDTO the updated muscle group data
     * @return the updated muscle group
     * @throws IllegalArgumentException if the muscle group doesn't exist or name conflict
     */
    public MuscleGroupDTO updateMuscleGroup(Long id, MuscleGroupDTO muscleGroupDTO) {
        MuscleGroupEntity existingEntity = muscleGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Muscle group not found with id: " + id));

        // Normalize name to uppercase
        String normalizedName = muscleGroupDTO.getName().trim().toUpperCase();
        
        // Check if another muscle group with this name exists (excluding current one)
        Optional<MuscleGroupEntity> existingByName = muscleGroupRepository.findByNameIgnoreCase(normalizedName);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Muscle group with name '" + normalizedName + "' already exists");
        }

        existingEntity.setName(normalizedName);
        existingEntity.setDescription(muscleGroupDTO.getDescription());
        
        MuscleGroupEntity updatedEntity = muscleGroupRepository.save(existingEntity);
        return muscleGroupMapper.toDTO(updatedEntity);
    }

    /**
     * Delete a muscle group
     *
     * @param id the muscle group ID
     * @throws IllegalArgumentException if the muscle group doesn't exist
     * @throws IllegalStateException if the muscle group is referenced by exercises
     */
    public void deleteMuscleGroup(Long id) {
        MuscleGroupEntity entity = muscleGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Muscle group not found with id: " + id));

        // Check if any exercises reference this muscle group
        long exerciseCount = exerciseRepository.countByMuscleGroup(entity);
        if (exerciseCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete muscle group '" + entity.getName() + 
                    "' because it is referenced by " + exerciseCount + " exercise(s)");
        }
        
        muscleGroupRepository.delete(entity);
    }

    /**
     * Check if a muscle group exists by ID
     *
     * @param id the muscle group ID
     * @return true if the muscle group exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return muscleGroupRepository.existsById(id);
    }
}
