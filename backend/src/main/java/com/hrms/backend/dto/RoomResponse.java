package com.hrms.backend.dto;

import com.hrms.backend.entity.RoomStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        UUID propertyId,
        String propertyName,
        UUID roomTypeId,
        String roomTypeName,
        String roomNumber,
        Integer floorNumber,
        Integer capacity,
        RoomStatus currentStatus,
        String statusReason,
        OffsetDateTime statusChangedAt,
        String description,
        List<UUID> amenityIds,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
