package com.hrms.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class BranchResponse {

    private UUID id;
    private UUID organizationId;
    private String organizationName;
    private String name;
    private String code;
    private String description;
    private String email;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String county;
    private String country;
    private String postalCode;
    private String currencyCode;
    private String timezone;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
