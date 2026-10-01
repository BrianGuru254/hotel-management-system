package com.hrms.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationCreateRequest(
        @NotNull UUID propertyId,
        @NotNull UUID guestId,
        UUID roomTypeId,
        UUID roomId,
        @NotNull @FutureOrPresent LocalDate checkInDate,
        @NotNull LocalDate checkOutDate,
        @NotNull @Min(1) Integer adults,
        @NotNull @Min(0) Integer children,
        @NotNull @Min(1) Integer numberOfRooms,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal nightlyRate,
        @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal depositAmount,
        @Size(max = 50) String bookingSource,
        @Size(max = 5000) String specialRequests) {
}
