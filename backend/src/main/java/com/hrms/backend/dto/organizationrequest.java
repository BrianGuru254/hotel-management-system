package com.hrms.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 200)
    private String legalName;

    @Size(max = 100)
    private String registrationNumber;

    @Size(max = 100)
    private String taxNumber;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String website;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String county;

    @Size(max = 100)
    private String country;

    @Size(max = 30)
    private String postalCode;

    @Pattern(regexp = "^[A-Za-z]{3}$")
    @Size(min = 3, max = 3)
    private String currencyCode;

    @Size(max = 100)
    private String timezone;

    private Boolean active;
}
