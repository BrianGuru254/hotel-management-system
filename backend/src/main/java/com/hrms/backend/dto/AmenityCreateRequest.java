package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AmenityCreateRequest(
        @NotNull UUID propertyId,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 5000) String description) {
}
