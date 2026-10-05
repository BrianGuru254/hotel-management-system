package com.hrms.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RestaurantTableRequest(@NotBlank @Size(max = 50) String tableNumber,
        @NotNull @Min(1) Integer capacity, @Size(max = 120) String location) { }
