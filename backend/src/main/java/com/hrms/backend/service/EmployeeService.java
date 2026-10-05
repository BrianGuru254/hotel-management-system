package com.hrms.backend.service;

import com.hrms.backend.dto.EmployeeRequest;
import com.hrms.backend.dto.EmployeeResponse;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service @Transactional
public class EmployeeService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final UUID NO_SCOPE = new UUID(0, 0);
    private static final Set<String> SORTS = Set.of("employeeNumber", "firstName", "lastName", "employmentStatus", "hireDate", "createdAt");
    private final EmployeeRepository employees;
    private final BranchRepository branches;
    private final DepartmentRepository departments;
    private final HmsUserRepository users;
    private final HousekeepingTaskRepository housekeeping;
    private final MaintenanceRequestRepository maintenance;
    private final ScopeAuthorizationService authorization;

    public EmployeeService(EmployeeRepository employees, BranchRepository branches, DepartmentRepository departments,
            HmsUserRepository users, HousekeepingTaskRepository housekeeping,
            MaintenanceRequestRepository maintenance, ScopeAuthorizationService authorization) {
        this.employees = employees; this.branches = branches; this.departments = departments;
        this.users = users; this.housekeeping = housekeeping; this.maintenance = maintenance; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findAll(UUID organizationId, UUID branchId, UUID departmentId,
            EmploymentStatus status, String search, int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        if (!SORTS.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        Pageable pageable = PageRequest.of(page, size, Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
        ScopeVisibility scope = authorization.visibility("EMPLOYEE_READ");
        if (!scope.systemWide() && scope.organizationIds().isEmpty() && scope.branchIds().isEmpty()
                && scope.propertyIds().isEmpty() && scope.departmentIds().isEmpty()) return Page.empty(pageable);
        return employees.search(scope.systemWide(), safe(scope.organizationIds()), safe(scope.branchIds()),
                safe(scope.propertyIds()), safe(scope.departmentIds()), organizationId, branchId,
                departmentId, status, search == null || search.isBlank() ? null : search.trim().toLowerCase(Locale.ROOT), pageable)
                .map(this::response);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(UUID id) {
        Employee employee = requireEmployee(id); authorize("EMPLOYEE_READ", employee); return response(employee);
    }

    public EmployeeResponse create(EmployeeRequest body) {
        Branch branch = branches.findById(body.branchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", body.branchId()));
        authorization.requireCapability("EMPLOYEE_CREATE", branch.getOrganization().getId(), branch.getId(), null, null);
        Department department = requireDepartment(body.departmentId(), branch);
        HmsUser user = requireUser(body.userId(), branch, department);
        String number = body.employeeNumber().trim();
        if (employees.existsByOrganizationIdAndEmployeeNumberIgnoreCaseAndDeletedAtIsNull(branch.getOrganization().getId(), number))
            throw new DuplicateResourceException("Employee number already exists in this organization");
        String email = normalizeEmail(body.email());
        ensureEmailAvailable(branch.getOrganization().getId(), email, null);
        Employee employee = new Employee(); employee.setOrganization(branch.getOrganization()); employee.setBranch(branch);
        employee.setDepartment(department); employee.setUser(user); employee.setEmployeeNumber(number);
        employee.setFirstName(body.firstName().trim()); employee.setLastName(body.lastName().trim()); employee.setEmail(email);
        employee.setPhone(normalize(body.phone())); employee.setJobTitle(normalize(body.jobTitle()));
        employee.setEmploymentStatus(body.employmentStatus() == null ? EmploymentStatus.ACTIVE : body.employmentStatus());
        employee.setHireDate(body.hireDate());
        return response(employees.save(employee));
    }

    public EmployeeResponse update(UUID id, EmployeeRequest body) {
        Employee employee = requireEmployee(id); authorize("EMPLOYEE_UPDATE", employee);
        Branch branch = branches.findById(body.branchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", body.branchId()));
        authorization.requireCapability("EMPLOYEE_UPDATE", branch.getOrganization().getId(), branch.getId(), null, null);
        Department department = requireDepartment(body.departmentId(), branch);
        HmsUser user = requireUser(body.userId(), branch, department);
        if (!employee.getBranch().getId().equals(branch.getId())
                || !Objects.equals(employee.getDepartment() == null ? null : employee.getDepartment().getId(), department == null ? null : department.getId()))
            requireNoActiveAssignments(employee);
        String number = body.employeeNumber().trim();
        if (employees.existsByOrganizationIdAndEmployeeNumberIgnoreCaseAndIdNotAndDeletedAtIsNull(branch.getOrganization().getId(), number, id))
            throw new DuplicateResourceException("Employee number already exists in this organization");
        String email = normalizeEmail(body.email()); ensureEmailAvailable(branch.getOrganization().getId(), email, id);
        employee.setOrganization(branch.getOrganization()); employee.setBranch(branch); employee.setDepartment(department);
        employee.setUser(user); employee.setEmployeeNumber(number); employee.setFirstName(body.firstName().trim());
        employee.setLastName(body.lastName().trim()); employee.setEmail(email); employee.setPhone(normalize(body.phone()));
        employee.setJobTitle(normalize(body.jobTitle()));
        if (body.employmentStatus() != null) employee.setEmploymentStatus(body.employmentStatus());
        if (employee.getEmploymentStatus() != EmploymentStatus.ACTIVE) requireNoActiveAssignments(employee);
        employee.setHireDate(body.hireDate());
        return response(employees.save(employee));
    }

    public void delete(UUID id) {
        Employee employee = requireEmployee(id); authorize("EMPLOYEE_UPDATE", employee);
        requireNoActiveAssignments(employee);
        employee.setEmploymentStatus(EmploymentStatus.INACTIVE); employee.setDeletedAt(OffsetDateTime.now());
        employees.save(employee);
    }

    private void requireNoActiveAssignments(Employee employee) {
        UUID id = employee.getId();
        if (housekeeping.existsByAssignedEmployeeIdAndStatusIn(id, List.of(HousekeepingTaskStatus.ASSIGNED, HousekeepingTaskStatus.IN_PROGRESS))
                || maintenance.existsByAssignedEmployeeIdAndStatusIn(id, List.of(MaintenanceStatus.ASSIGNED, MaintenanceStatus.IN_PROGRESS)))
            throw new InvalidRelationshipException("Reassign active housekeeping or maintenance work before deactivating this employee");
    }

    private Department requireDepartment(UUID departmentId, Branch branch) {
        if (departmentId == null) return null;
        Department department = departments.findById(departmentId).orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
        if (!department.getBranch().getId().equals(branch.getId()) || !department.getOrganization().getId().equals(branch.getOrganization().getId()))
            throw new InvalidRelationshipException("Department does not belong to the employee branch");
        if (!Boolean.TRUE.equals(department.getActive())) throw new InvalidRelationshipException("An inactive department cannot be assigned");
        return department;
    }

    private HmsUser requireUser(UUID userId, Branch branch, Department department) {
        if (userId == null) return null;
        HmsUser user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (!Boolean.TRUE.equals(user.getActive())) throw new InvalidRelationshipException("An inactive user cannot be linked to an employee");
        UUID propertyId = department == null ? null : department.getProperty().getId();
        UUID departmentId = department == null ? null : department.getId();
        boolean scoped = user.getRoleScopes().stream().anyMatch(g -> switch (g.getScopeType()) {
            case SYSTEM -> true;
            case ORGANIZATION -> g.getOrganization() != null && g.getOrganization().getId().equals(branch.getOrganization().getId());
            case BRANCH -> g.getBranch() != null && g.getBranch().getId().equals(branch.getId());
            case PROPERTY -> propertyId != null && g.getProperty() != null && g.getProperty().getId().equals(propertyId);
            case DEPARTMENT -> departmentId != null && g.getDepartment() != null && g.getDepartment().getId().equals(departmentId);
            case SELF -> false;
        });
        if (!scoped) throw new InvalidRelationshipException("Linked user must have a scope within the employee's organization or branch");
        return user;
    }

    private Employee requireEmployee(UUID id) {
        return employees.findById(id).filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
    private void authorize(String capability, Employee e) {
        authorization.requireCapability(capability, e.getOrganization().getId(), e.getBranch().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getProperty().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getId());
    }
    private List<UUID> safe(Set<UUID> values) { return values.isEmpty() ? List.of(NO_SCOPE) : List.copyOf(values); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String normalizeEmail(String value) { String v = normalize(value); return v == null ? null : v.toLowerCase(Locale.ROOT); }
    private void ensureEmailAvailable(UUID orgId, String email, UUID currentId) {
        if (email == null) return;
        boolean exists = currentId == null ? employees.existsByOrganizationIdAndEmailIgnoreCaseAndDeletedAtIsNull(orgId, email)
                : employees.existsByOrganizationIdAndEmailIgnoreCaseAndIdNotAndDeletedAtIsNull(orgId, email, currentId);
        if (exists) throw new DuplicateResourceException("Employee email already exists in this organization");
    }
    private EmployeeResponse response(Employee e) {
        return new EmployeeResponse(e.getId(), e.getOrganization().getId(), e.getBranch().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getName(),
                e.getUser() == null ? null : e.getUser().getId(), e.getEmployeeNumber(), e.getFirstName(),
                e.getLastName(), e.getEmail(), e.getPhone(), e.getJobTitle(), e.getEmploymentStatus(),
                e.getHireDate(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
