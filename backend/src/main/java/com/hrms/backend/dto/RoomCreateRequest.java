package com.hrms.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RoomCreateRequest(
        @NotNull UUID propertyId,
        @NotNull UUID roomTypeId,
        @NotBlank @Size(max = 30) String roomNumber,
        Integer floorNumber,
        @Min(1) Integer capacity,
        @Size(max = 5000) String description) {
}
