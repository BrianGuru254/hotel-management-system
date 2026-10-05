package com.hrms.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StayGuestResponse(UUID guestId, String guestName, boolean primaryGuest, OffsetDateTime addedAt) {
}
