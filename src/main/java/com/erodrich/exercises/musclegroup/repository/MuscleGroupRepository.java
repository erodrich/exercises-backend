package com.erodrich.exercises.musclegroup.repository;

import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MuscleGroupRepository extends JpaRepository<MuscleGroupEntity, Long> {

    /**
     * Find a muscle group by its name (case-insensitive)
     *
     * @param name the name to search for
     * @return an Optional containing the muscle group if found
     */
    Optional<MuscleGroupEntity> findByNameIgnoreCase(String name);

    /**
     * Check if a muscle group exists with the given name (case-insensitive)
     *
     * @param name the name to check
     * @return true if a muscle group exists with this name
     */
    boolean existsByNameIgnoreCase(String name);
}
