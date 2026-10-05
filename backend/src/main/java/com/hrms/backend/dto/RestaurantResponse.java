package com.hrms.backend.dto;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RestaurantResponse(UUID id, UUID branchId, String name, String description,
        LocalTime openingTime, LocalTime closingTime, Boolean active, OffsetDateTime createdAt) { }
