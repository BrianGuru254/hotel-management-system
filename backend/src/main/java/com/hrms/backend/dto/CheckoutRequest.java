package com.hrms.backend.dto;

import jakarta.validation.constraints.Size;

public record CheckoutRequest(@Size(max = 5000) String notes, boolean completeReservation) {
}
