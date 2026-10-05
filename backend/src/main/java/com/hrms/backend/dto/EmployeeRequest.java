package com.hrms.backend.dto;

import com.hrms.backend.entity.EmploymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeRequest(@NotNull UUID branchId, UUID departmentId, UUID userId,
        @NotBlank @Size(max = 50) String employeeNumber,
        @NotBlank @Size(max = 100) String firstName, @NotBlank @Size(max = 100) String lastName,
        @Email @Size(max = 255) String email, @Size(max = 50) String phone,
        @Size(max = 150) String jobTitle, EmploymentStatus employmentStatus, LocalDate hireDate) { }
