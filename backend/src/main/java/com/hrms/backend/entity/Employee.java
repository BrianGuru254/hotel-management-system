package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employees_org_status", columnList = "organization_id,employment_status"),
        @Index(name = "idx_employees_branch", columnList = "branch_id"),
        @Index(name = "idx_employees_department", columnList = "department_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_employees_org_scope", columnNames = {"organization_id", "id"}),
        @UniqueConstraint(name = "uq_employees_org_branch_id", columnNames = {"organization_id", "branch_id", "id"}),
        @UniqueConstraint(name = "uq_employees_org_number", columnNames = {"organization_id", "employee_number"}),
        @UniqueConstraint(name = "uq_employees_user", columnNames = {"user_id"})
})
@Getter @Setter
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false) private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "branch_id", nullable = false) private Branch branch;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id") private Department department;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private HmsUser user;
    @Column(name = "employee_number", nullable = false, length = 50) private String employeeNumber;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(length = 255) private String email;
    @Column(length = 50) private String phone;
    @Column(name = "job_title", length = 150) private String jobTitle;
    @Enumerated(EnumType.STRING) @Column(name = "employment_status", nullable = false, length = 30) private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;
    @Column(name = "hire_date") private LocalDate hireDate;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @Column(name = "deleted_at") private OffsetDateTime deletedAt;
    @PrePersist void onCreate() { OffsetDateTime now = OffsetDateTime.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
