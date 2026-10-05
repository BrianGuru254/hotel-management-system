package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuCategoryRequest(@NotBlank @Size(max = 120) String name, @Size(max = 4000) String description,
        Integer displayOrder, Boolean active) { }
