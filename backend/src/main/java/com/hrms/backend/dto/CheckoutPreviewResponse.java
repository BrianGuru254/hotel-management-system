package com.hrms.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutPreviewResponse(
        UUID stayId,
        String stayNumber,
        long nights,
        BigDecimal nightlyRate,
        BigDecimal accommodationEstimate,
        BigDecimal depositEstimate,
        BigDecimal estimatedBalanceDue,
        String currencyCode) {
}
