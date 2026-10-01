package com.hrms.backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReservationAvailabilityResponse(
        UUID propertyId,
        UUID roomTypeId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int requestedRooms,
        int totalAvailableRooms,
        int alreadyReservedRooms,
        int availableRooms,
        boolean available) {
}
