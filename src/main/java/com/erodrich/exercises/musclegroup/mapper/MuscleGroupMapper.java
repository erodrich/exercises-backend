package com.erodrich.exercises.musclegroup.mapper;

import com.erodrich.exercises.musclegroup.dto.MuscleGroupDTO;
import com.erodrich.exercises.musclegroup.entity.MuscleGroupEntity;
import org.springframework.stereotype.Component;

@Component
public class MuscleGroupMapper {

    /**
     * Converts a MuscleGroupEntity to a MuscleGroupDTO
     *
     * @param entity the entity to convert
     * @return the converted DTO, or null if entity is null
     */
    public MuscleGroupDTO toDTO(MuscleGroupEntity entity) {
        if (entity == null) {
            return null;
        }

        MuscleGroupDTO dto = new MuscleGroupDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        
        return dto;
    }

    /**
     * Converts a MuscleGroupDTO to a MuscleGroupEntity
     *
     * @param dto the DTO to convert
     * @return the converted entity, or null if dto is null
     */
    public MuscleGroupEntity toEntity(MuscleGroupDTO dto) {
        if (dto == null) {
            return null;
        }

        MuscleGroupEntity entity = new MuscleGroupEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        
        return entity;
    }

    /**
     * Updates an existing entity with data from a DTO
     *
     * @param entity the entity to update
     * @param dto the DTO containing new data
     */
    public void updateEntityFromDTO(MuscleGroupEntity entity, MuscleGroupDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }
}
