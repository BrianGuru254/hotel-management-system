package com.hrms.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GuestUpdateRequest(
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String middleName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 30) String phone,
        @Email @Size(max = 150) String email,
        @Size(max = 100) String nationality,
        @Pattern(regexp = "(?i)[A-Z][A-Z0-9_]{0,49}") String identificationType,
        @Size(max = 100) String identificationNumber,
        @Size(max = 200) String addressLine1,
        @Size(max = 200) String addressLine2,
        @Size(max = 100) String city,
        @Size(max = 100) String county,
        @Size(max = 100) String country,
        @Past LocalDate dateOfBirth,
        @Size(max = 150) String emergencyContactName,
        @Size(max = 30) String emergencyContactPhone,
        @Size(max = 5000) String notes) {

    @AssertTrue(message = "Identification type and number must be provided together")
    public boolean isIdentificationDetailsComplete() {
        boolean hasType = identificationType != null && !identificationType.isBlank();
        boolean hasNumber = identificationNumber != null && !identificationNumber.isBlank();
        return hasType == hasNumber;
    }
}
