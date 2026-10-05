package com.hrms.backend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MenuItemResponse(UUID id, UUID restaurantId, UUID categoryId, String categoryName,
        String name, String sku, String description, BigDecimal sellingPrice, BigDecimal taxRate,
        int preparationTimeMinutes, boolean available, OffsetDateTime createdAt) { }
