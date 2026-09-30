package com.hrms.backend.dto;

import com.hrms.backend.entity.ScopeType;

import java.util.UUID;

public record UserRoleScopeResponse(UUID id, String roleKey, ScopeType scopeType,
                                    UUID organizationId, UUID branchId, UUID propertyId, UUID departmentId) {
}
