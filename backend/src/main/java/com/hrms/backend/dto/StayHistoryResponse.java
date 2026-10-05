package com.hrms.backend.dto;

import com.hrms.backend.entity.StayStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StayHistoryResponse(
        UUID id,
        StayStatus previousStatus,
        StayStatus newStatus,
        String reason,
        UUID changedBy,
        OffsetDateTime changedAt) {
}
