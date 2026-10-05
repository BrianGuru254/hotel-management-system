package com.hrms.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MaintenanceAssignRequest(@NotNull UUID userId) { }
