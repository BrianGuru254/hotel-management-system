package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DepartmentRequest(
        @NotNull UUID organizationId,
        @NotNull UUID branchId,
        @NotNull UUID propertyId,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 50) @Pattern(regexp = "^[A-Za-z0-9_-]+$") String code,
        Boolean active) {
}
