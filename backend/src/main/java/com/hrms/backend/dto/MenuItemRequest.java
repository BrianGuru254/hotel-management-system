package com.hrms.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemRequest(@NotNull UUID categoryId, @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 50) String sku, @Size(max = 4000) String description,
        @NotNull @DecimalMin("0.00") @DecimalMax("9999999999.99") BigDecimal sellingPrice,
        @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal taxRate,
        @Min(0) Integer preparationTimeMinutes) { }
