package com.hrms.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record RoomAmenitiesRequest(
        @NotNull @Size(max = 100) List<@NotNull UUID> amenityIds) {
}
