package com.hrms.backend.dto;

import com.hrms.backend.entity.RestaurantTableStatus;
import jakarta.validation.constraints.NotNull;

public record RestaurantTableStatusRequest(@NotNull RestaurantTableStatus status) { }
