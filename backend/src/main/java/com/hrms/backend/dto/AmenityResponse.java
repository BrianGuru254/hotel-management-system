package com.hrms.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmenityResponse(
        UUID id,
        UUID propertyId,
        String propertyName,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
