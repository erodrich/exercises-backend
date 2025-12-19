package com.erodrich.exercises.musclegroup;

import com.erodrich.exercises.musclegroup.dto.MuscleGroupDTO;
import com.erodrich.exercises.musclegroup.service.MuscleGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public REST controller for muscle group read operations.
 * These endpoints are accessible to all users (authenticated or not)
 * to allow viewing available muscle groups when creating exercises.
 */
@RestController
@RequestMapping("/api/v1/muscle-groups")
public class MuscleGroupPublicBoundary {

    private final MuscleGroupService muscleGroupService;

    public MuscleGroupPublicBoundary(MuscleGroupService muscleGroupService) {
        this.muscleGroupService = muscleGroupService;
    }

    /**
     * Get all muscle groups (public endpoint)
     *
     * @return list of all muscle groups
     */
    @GetMapping
    public ResponseEntity<List<MuscleGroupDTO>> getAllMuscleGroups() {
        List<MuscleGroupDTO> muscleGroups = muscleGroupService.getAllMuscleGroups();
        return ResponseEntity.ok(muscleGroups);
    }

    /**
     * Get a muscle group by ID (public endpoint)
     *
     * @param id the muscle group ID
     * @return the muscle group if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MuscleGroupDTO> getMuscleGroupById(@PathVariable Long id) {
        return muscleGroupService.getMuscleGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a muscle group by name (public endpoint)
     *
     * @param name the muscle group name
     * @return the muscle group if found
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<MuscleGroupDTO> getMuscleGroupByName(@PathVariable String name) {
        return muscleGroupService.getMuscleGroupByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
