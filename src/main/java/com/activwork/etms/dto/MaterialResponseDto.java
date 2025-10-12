package com.activwork.etms.dto;

import com.activwork.etms.model.Material;
import com.activwork.etms.model.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * DTO for material response data.
 * Returns material information for display and download.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponseDto {

    private UUID id;
    private UUID courseId;
    
    private String filename;
    private String originalFilename;
    private MaterialType materialType;
    private Long fileSize;
    
    private Integer durationSeconds; // For videos
    private String thumbnailUrl;
    
    private Integer downloadCount;
    private Integer viewCount;
    
    private Boolean isDownloadable;
    private Boolean isRequired;
    private Integer displayOrder;
    
    private String description;
    private ZonedDateTime uploadedAt;

    /**
     * Static factory method to create MaterialResponseDto from Material entity.
     */
    public static MaterialResponseDto fromEntity(Material material) {
        MaterialResponseDto dto = new MaterialResponseDto();
        dto.setId(material.getId());
        
        if (material.getCourse() != null) {
            dto.setCourseId(material.getCourse().getId());
        }
        
        dto.setFilename(material.getFilename());
        dto.setOriginalFilename(material.getOriginalFilename());
        dto.setMaterialType(material.getMaterialType());
        dto.setFileSize(material.getFileSize());
        
        dto.setDurationSeconds(material.getDurationSeconds());
        dto.setThumbnailUrl(material.getThumbnailUrl());
        
        dto.setDownloadCount(material.getDownloadCount());
        dto.setViewCount(material.getViewCount());
        
        dto.setIsDownloadable(material.getIsDownloadable());
        dto.setIsRequired(material.getIsRequired());
        dto.setDisplayOrder(material.getDisplayOrder());
        
        dto.setDescription(material.getDescription());
        dto.setUploadedAt(material.getUploadedAt());
        
        return dto;
    }
}

