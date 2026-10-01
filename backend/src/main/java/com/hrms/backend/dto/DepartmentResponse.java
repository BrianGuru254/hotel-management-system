package com.hrms.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartmentResponse(UUID id, UUID organizationId, UUID branchId, UUID propertyId,
                                String name, String code, boolean active,
                                OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
