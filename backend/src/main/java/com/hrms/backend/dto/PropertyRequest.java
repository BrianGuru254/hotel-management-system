package com.hrms.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PropertyRequest {
    @NotNull private UUID organizationId;
    @NotNull private UUID branchId;
    @NotBlank @Size(max = 150) private String name;
    @NotBlank @Size(max = 50) private String code;
    private String description;
    @Email @Size(max = 150) private String email;
    @Size(max = 50) private String phone;
    @Size(max = 255) private String addressLine1;
    @Size(max = 255) private String addressLine2;
    @Size(max = 100) private String city;
    @Size(max = 100) private String county;
    @Size(max = 100) private String country;
    @Size(max = 30) private String postalCode;
    @Pattern(regexp = "^[A-Za-z]{3}$") @Size(min = 3, max = 3) private String currencyCode;
    @Size(max = 100) private String timezone;
    private Boolean active;
}
