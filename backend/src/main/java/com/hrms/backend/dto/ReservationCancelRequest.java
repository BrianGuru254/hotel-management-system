package com.hrms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReservationCancelRequest(
        @NotBlank @Size(max = 5000) String reason) {
}
