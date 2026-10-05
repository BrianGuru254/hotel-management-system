package com.hrms.backend.dto;

import jakarta.validation.constraints.Size;

public record OrderStatusRequest(@Size(max = 1000) String reason) { }
