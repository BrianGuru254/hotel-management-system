package com.hrms.backend.dto;

import com.hrms.backend.entity.EmploymentStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EmployeeResponse(UUID id, UUID organizationId, UUID branchId, UUID departmentId,
        String departmentName, UUID userId, String employeeNumber, String firstName, String lastName,
        String email, String phone, String jobTitle, EmploymentStatus employmentStatus,
        LocalDate hireDate, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
