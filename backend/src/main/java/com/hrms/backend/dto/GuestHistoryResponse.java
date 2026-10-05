package com.hrms.backend.dto;

import com.hrms.backend.entity.ReservationStatus;
import com.hrms.backend.entity.StayStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GuestHistoryResponse(UUID guestId, UUID propertyId,
        List<ReservationRecord> reservations, List<StayRecord> stays,
        List<String> availableSections, List<String> unavailableSections) {
    public record ReservationRecord(UUID id, String reservationNumber, LocalDate checkInDate,
            LocalDate checkOutDate, int numberOfRooms, ReservationStatus status) { }
    public record StayRecord(UUID id, String stayNumber, String roomNumber, OffsetDateTime checkIn,
            OffsetDateTime checkOut, StayStatus status) { }
}
