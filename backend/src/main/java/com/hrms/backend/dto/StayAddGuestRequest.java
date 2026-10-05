package com.hrms.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StayAddGuestRequest(@NotNull UUID guestId, boolean isPrimaryGuest) {
}
