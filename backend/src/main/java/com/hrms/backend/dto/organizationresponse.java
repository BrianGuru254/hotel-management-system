package com.hrms.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrganizationResponse {

    private UUID id;
    private String name;
    private String legalName;
    private String registrationNumber;
    private String taxNumber;
    private String email;
    private String phone;
    private String website;
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
