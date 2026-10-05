package com.hrms.backend.dto;

import com.hrms.backend.entity.MaintenancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MaintenanceCreateRequest(@NotNull UUID propertyId, UUID roomId,
        @NotBlank @Size(max = 200) String title, @Size(max = 8000) String description,
        MaintenancePriority priority) { }
