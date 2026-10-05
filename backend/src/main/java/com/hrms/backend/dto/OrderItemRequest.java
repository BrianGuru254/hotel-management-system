package com.hrms.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record OrderItemRequest(@NotNull UUID menuItemId, @NotNull @Min(1) @Max(1000) Integer quantity,
                               @Size(max = 1000) String specialInstructions) { }
