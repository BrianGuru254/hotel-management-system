package com.hrms.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantOrderItemResponse(UUID id, UUID menuItemId, String name, String sku, int quantity,
        BigDecimal unitPrice, BigDecimal taxRate, BigDecimal subtotal, BigDecimal taxAmount,
        BigDecimal total, String specialInstructions) { }
