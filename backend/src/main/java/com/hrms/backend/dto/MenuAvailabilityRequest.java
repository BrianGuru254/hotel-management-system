package com.hrms.backend.dto;

import jakarta.validation.constraints.NotNull;

public record MenuAvailabilityRequest(@NotNull Boolean available) { }
