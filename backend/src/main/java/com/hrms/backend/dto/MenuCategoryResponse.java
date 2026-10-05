package com.hrms.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MenuCategoryResponse(UUID id, UUID restaurantId, String name, String description,
        int displayOrder, boolean active, OffsetDateTime createdAt) { }
