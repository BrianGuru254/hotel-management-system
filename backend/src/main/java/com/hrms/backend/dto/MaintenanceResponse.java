package com.hrms.backend.dto;

import com.hrms.backend.entity.MaintenancePriority;
import com.hrms.backend.entity.MaintenanceStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MaintenanceResponse(UUID id, UUID propertyId, UUID roomId, String roomNumber,
        UUID reportedBy, UUID assignedEmployeeId, String title, String description, String resolution,
        MaintenancePriority priority, MaintenanceStatus status, OffsetDateTime reportedAt,
        OffsetDateTime assignedAt, OffsetDateTime startedAt, OffsetDateTime resolvedAt,
        OffsetDateTime closedAt) { }
