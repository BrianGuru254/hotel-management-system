package com.hrms.backend.dto;

import com.hrms.backend.entity.ScopeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ScopeGrantRequest(
        @NotBlank String roleKey,
        @NotNull ScopeType scopeType,
        UUID organizationId,
        UUID branchId,
        UUID propertyId,
        UUID departmentId) {
}
