package com.hrms.backend.dto;

import com.hrms.backend.entity.MaintenanceStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MaintenanceHistoryResponse(UUID id, MaintenanceStatus previousStatus,
        MaintenanceStatus newStatus, String reason, UUID changedBy, OffsetDateTime changedAt) { }
