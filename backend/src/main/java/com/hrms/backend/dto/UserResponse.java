package com.hrms.backend.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String email, String firstName, String lastName, boolean active,
                           List<UserRoleScopeResponse> roleScopes, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
