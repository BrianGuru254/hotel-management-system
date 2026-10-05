package com.hrms.backend.dto;

import com.hrms.backend.entity.RestaurantTableStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RestaurantTableResponse(UUID id, UUID restaurantId, String tableNumber,
        int capacity, String location, RestaurantTableStatus status, OffsetDateTime createdAt) { }
