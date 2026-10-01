package com.hrms.backend.dto;

import com.hrms.backend.entity.RoomTypeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomTypeCreateRequest(
        @NotNull UUID propertyId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9_-]{0,49}") String code,
        @Size(max = 5000) String description,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal basePrice,
        @Min(1) Integer maxOccupancy,
        @DecimalMin("0.01") @Digits(integer = 6, fraction = 2) BigDecimal sizeSquareMeters,
        RoomTypeStatus status) {
}
