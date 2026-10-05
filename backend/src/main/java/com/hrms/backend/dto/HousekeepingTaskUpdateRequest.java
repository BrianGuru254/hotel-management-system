package com.hrms.backend.dto;

import com.hrms.backend.entity.HousekeepingPriority;
import com.hrms.backend.entity.HousekeepingTaskStatus;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record HousekeepingTaskUpdateRequest(UUID assignedUserId, HousekeepingTaskStatus status,
        HousekeepingPriority priority, @Size(max = 4000) String notes) { }
