package com.hrms.backend.dto;

import com.hrms.backend.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        String reservationNumber,
        UUID propertyId,
        UUID guestId,
        String guestName,
        UUID roomTypeId,
        String roomTypeName,
        UUID roomId,
        String roomNumber,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int adults,
        int children,
        int numberOfRooms,
        BigDecimal nightlyRate,
        BigDecimal estimatedTotal,
        BigDecimal depositAmount,
        String currencyCode,
        String bookingSource,
        String specialRequests,
        ReservationStatus status,
        OffsetDateTime cancelledAt,
        String cancellationReason,
        UUID createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
