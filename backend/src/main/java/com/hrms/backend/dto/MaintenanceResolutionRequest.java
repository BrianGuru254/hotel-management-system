package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaintenanceResolutionRequest(@NotBlank @Size(max = 8000) String resolution) { }
