package com.hrms.backend.dto;

import com.hrms.backend.entity.HousekeepingPriority;
import com.hrms.backend.entity.HousekeepingTaskStatus;
import com.hrms.backend.entity.HousekeepingTaskType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record HousekeepingTaskResponse(UUID id, UUID propertyId, UUID roomId, String roomNumber,
        UUID assignedUserId, HousekeepingTaskType taskType, HousekeepingTaskStatus status,
        HousekeepingPriority priority, String notes, OffsetDateTime assignedAt,
        OffsetDateTime startedAt, OffsetDateTime completedAt, OffsetDateTime createdAt) { }
