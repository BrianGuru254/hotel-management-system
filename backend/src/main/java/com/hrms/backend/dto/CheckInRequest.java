package com.hrms.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CheckInRequest(
        UUID reservationId,
        UUID propertyId,
        UUID guestId,
        UUID roomId,
        LocalDate expectedCheckOutDate,
        @Min(1) Integer adults,
        @Min(0) Integer children,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal nightlyRate,
        @Size(max = 5000) String notes) {
}
