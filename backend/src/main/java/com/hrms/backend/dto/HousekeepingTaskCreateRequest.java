package com.hrms.backend.dto;

import com.hrms.backend.entity.HousekeepingPriority;
import com.hrms.backend.entity.HousekeepingTaskType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record HousekeepingTaskCreateRequest(@NotNull UUID propertyId, @NotNull UUID roomId,
        HousekeepingTaskType taskType, HousekeepingPriority priority, @Size(max = 4000) String notes) { }
