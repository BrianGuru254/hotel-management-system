package com.hrms.backend.dto;

import com.hrms.backend.entity.StayStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StayResponse(
        UUID id,
        String stayNumber,
        UUID propertyId,
        UUID reservationId,
        UUID guestId,
        String guestName,
        UUID roomId,
        String roomNumber,
        OffsetDateTime actualCheckIn,
        LocalDate expectedCheckOutDate,
        OffsetDateTime actualCheckOut,
        int adults,
        int children,
        BigDecimal nightlyRate,
        BigDecimal depositAmount,
        String currencyCode,
        StayStatus status,
        String notes,
        String checkoutNotes,
        List<StayGuestResponse> guests,
        UUID checkedInBy,
        UUID checkedOutBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
