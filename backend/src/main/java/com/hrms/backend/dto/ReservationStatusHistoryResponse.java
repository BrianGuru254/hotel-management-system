package com.hrms.backend.dto;

import com.hrms.backend.entity.ReservationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReservationStatusHistoryResponse(
        UUID id,
        ReservationStatus previousStatus,
        ReservationStatus newStatus,
        String reason,
        UUID changedBy,
        OffsetDateTime changedAt) {
}
