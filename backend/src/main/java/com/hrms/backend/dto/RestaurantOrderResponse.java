package com.hrms.backend.dto;

import com.hrms.backend.entity.RestaurantOrderStatus;
import com.hrms.backend.entity.RestaurantOrderType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RestaurantOrderResponse(UUID id, UUID restaurantId, UUID tableId, UUID guestId, UUID stayId,
        UUID waiterId, RestaurantOrderType orderType, RestaurantOrderStatus status, List<RestaurantOrderItemResponse> items,
        BigDecimal subtotal, BigDecimal taxAmount, BigDecimal discountAmount, BigDecimal total, String notes,
        OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
