package com.hrms.backend.dto;

import com.hrms.backend.entity.RoomTypeStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RoomTypeResponse(
        UUID id,
        UUID propertyId,
        String propertyName,
        String currencyCode,
        String name,
        String code,
        String description,
        BigDecimal basePrice,
        Integer maxOccupancy,
        BigDecimal sizeSquareMeters,
        RoomTypeStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
