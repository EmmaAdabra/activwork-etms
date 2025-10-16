package com.activwork.etms.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new course section.
 * Used in course creation and editing forms.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionCreateDto {
    
    @NotBlank(message = "Section title is required")
    @Size(min = 1, max = 255, message = "Section title must be between 1 and 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Section order is required")
    @Min(value = 0, message = "Section order must be non-negative")
    private Integer sectionOrder;
    
    private Boolean isActive = true;
    
    // Material IDs to assign to this section (for editing)
    private List<UUID> materialIds = new ArrayList<>();
}

