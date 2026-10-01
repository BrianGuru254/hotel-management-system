package com.hrms.backend.service;

import com.hrms.backend.dto.DepartmentRequest;
import com.hrms.backend.dto.DepartmentResponse;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departments;
    private final OrganizationRepository organizations;
    private final BranchRepository branches;
    private final PropertyRepository properties;
    private final ScopeAuthorizationService authorization;

    public DepartmentService(DepartmentRepository departments, OrganizationRepository organizations,
                             BranchRepository branches, PropertyRepository properties,
                             ScopeAuthorizationService authorization) {
        this.departments = departments;
        this.organizations = organizations;
        this.branches = branches;
        this.properties = properties;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll(UUID propertyId) {
        ScopeVisibility visibility = authorization.visibility("DEPARTMENT_READ");
        Map<UUID, Department> found = new LinkedHashMap<>();
        if (visibility.systemWide()) {
            List<Department> records = propertyId == null ? departments.findAll() : departments.findByPropertyId(propertyId);
            records.forEach(item -> found.put(item.getId(), item));
        } else {
            if (!visibility.organizationIds().isEmpty()) departments.findByOrganizationIdIn(visibility.organizationIds()).forEach(d -> found.put(d.getId(), d));
            if (!visibility.branchIds().isEmpty()) departments.findByBranchIdIn(visibility.branchIds()).forEach(d -> found.put(d.getId(), d));
            if (!visibility.propertyIds().isEmpty()) departments.findByPropertyIdIn(visibility.propertyIds()).forEach(d -> found.put(d.getId(), d));
            if (!visibility.departmentIds().isEmpty()) departments.findByIdIn(visibility.departmentIds()).forEach(d -> found.put(d.getId(), d));
        }
        return found.values().stream().filter(d -> propertyId == null || d.getProperty().getId().equals(propertyId))
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(UUID id) {
        Department department = get(id);
        authorize("DEPARTMENT_READ", department);
        return toResponse(department);
    }

    public DepartmentResponse create(DepartmentRequest request) {
        authorization.requireCapability("DEPARTMENT_CREATE", request.organizationId(), request.branchId(), request.propertyId(), null);
        Organization organization = organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
        Branch branch = branches.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.branchId()));
        Property property = properties.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", request.propertyId()));
        validateHierarchy(organization, branch, property);
        String code = normalize(request.code());
        if (departments.existsByPropertyIdAndCodeIgnoreCase(property.getId(), code)) {
            throw new DuplicateResourceException("Department code already exists for this property");
        }
        Department department = new Department();
        department.setOrganization(organization);
        department.setBranch(branch);
        department.setProperty(property);
        department.setName(request.name().trim());
        department.setCode(code);
        if (request.active() != null) department.setActive(request.active());
        return toResponse(departments.save(department));
    }

    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        Department department = get(id);
        authorize("DEPARTMENT_UPDATE", department);
        authorization.requireCapability("DEPARTMENT_UPDATE", request.organizationId(), request.branchId(), request.propertyId(), id);
        Organization organization = organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
        Branch branch = branches.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.branchId()));
        Property property = properties.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", request.propertyId()));
        validateHierarchy(organization, branch, property);
        String code = normalize(request.code());
        if (departments.existsByPropertyIdAndCodeIgnoreCaseAndIdNot(property.getId(), code, id)) {
            throw new DuplicateResourceException("Department code already exists for this property");
        }
        department.setOrganization(organization);
        department.setBranch(branch);
        department.setProperty(property);
        department.setName(request.name().trim());
        department.setCode(code);
        if (request.active() != null) department.setActive(request.active());
        return toResponse(departments.save(department));
    }

    public DepartmentResponse setActive(UUID id, boolean active) {
        Department department = get(id);
        authorize("DEPARTMENT_UPDATE", department);
        department.setActive(active);
        return toResponse(departments.save(department));
    }

    public void delete(UUID id) {
        Department department = get(id);
        authorize("DEPARTMENT_DELETE", department);
        departments.delete(department);
    }

    private void authorize(String capability, Department department) {
        authorization.requireCapability(capability, department.getOrganization().getId(), department.getBranch().getId(),
                department.getProperty().getId(), department.getId());
    }

    private void validateHierarchy(Organization organization, Branch branch, Property property) {
        if (!branch.getOrganization().getId().equals(organization.getId())
                || !property.getOrganization().getId().equals(organization.getId())
                || !property.getBranch().getId().equals(branch.getId())) {
            throw new InvalidRelationshipException("The department property must belong to the supplied branch and organization");
        }
    }

    private Department get(UUID id) {
        return departments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    private String normalize(String code) { return code.trim().toUpperCase(Locale.ROOT); }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getOrganization().getId(), department.getBranch().getId(),
                department.getProperty().getId(), department.getName(), department.getCode(), department.getActive(),
                department.getCreatedAt(), department.getUpdatedAt());
    }
}
