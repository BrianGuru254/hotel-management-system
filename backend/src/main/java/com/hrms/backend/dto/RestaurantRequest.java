package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantRequest(@NotNull UUID branchId, @NotBlank @Size(max = 150) String name,
        @Size(max = 4000) String description, LocalTime openingTime, LocalTime closingTime, Boolean active) { }
