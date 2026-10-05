package com.hrms.backend.dto;

import com.hrms.backend.entity.RestaurantOrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(@NotNull UUID restaurantId, UUID tableId, UUID guestId, UUID stayId,
        @NotNull RestaurantOrderType orderType, @NotEmpty @Size(max = 100) List<@Valid OrderItemRequest> items,
        @Size(max = 2000) String notes) { }
