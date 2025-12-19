package com.erodrich.exercises.musclegroup;

import com.erodrich.exercises.musclegroup.dto.MuscleGroupDTO;
import com.erodrich.exercises.musclegroup.service.MuscleGroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/muscle-groups")
@PreAuthorize("hasRole('ADMIN')")
public class MuscleGroupBoundary {

    private final MuscleGroupService muscleGroupService;

    public MuscleGroupBoundary(MuscleGroupService muscleGroupService) {
        this.muscleGroupService = muscleGroupService;
    }

    /**
     * Get all muscle groups
     *
     * @return list of all muscle groups
     */
    @GetMapping
    public ResponseEntity<List<MuscleGroupDTO>> getAllMuscleGroups() {
        List<MuscleGroupDTO> muscleGroups = muscleGroupService.getAllMuscleGroups();
        return ResponseEntity.ok(muscleGroups);
    }

    /**
     * Get a muscle group by ID
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
     * Create a new muscle group
     *
     * @param muscleGroupDTO the muscle group data
     * @return the created muscle group
     */
    @PostMapping
    public ResponseEntity<?> createMuscleGroup(@Valid @RequestBody MuscleGroupDTO muscleGroupDTO) {
        try {
            MuscleGroupDTO created = muscleGroupService.createMuscleGroup(muscleGroupDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update an existing muscle group
     *
     * @param id the muscle group ID
     * @param muscleGroupDTO the updated muscle group data
     * @return the updated muscle group
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMuscleGroup(
            @PathVariable Long id,
            @Valid @RequestBody MuscleGroupDTO muscleGroupDTO) {
        try {
            MuscleGroupDTO updated = muscleGroupService.updateMuscleGroup(id, muscleGroupDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a muscle group
     *
     * @param id the muscle group ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMuscleGroup(@PathVariable Long id) {
        try {
            muscleGroupService.deleteMuscleGroup(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Simple error response class
     */
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
