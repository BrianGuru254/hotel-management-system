package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AmenityUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 5000) String description) {
}
