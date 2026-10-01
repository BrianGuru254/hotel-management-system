package com.hrms.backend.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record GuestResponse(
        UUID id,
        UUID propertyId,
        String guestNumber,
        String firstName,
        String middleName,
        String lastName,
        String phone,
        String email,
        String nationality,
        String identificationType,
        String identificationNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String county,
        String country,
        LocalDate dateOfBirth,
        String emergencyContactName,
        String emergencyContactPhone,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
