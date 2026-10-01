package com.hrms.backend.dto;

import com.hrms.backend.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomStatusChangeRequest(
        @NotNull RoomStatus status,
        @Size(max = 500) String reason) {
}
