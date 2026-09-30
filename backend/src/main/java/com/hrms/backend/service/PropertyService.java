package com.hrms.backend.service;

import com.hrms.backend.dto.PropertyRequest;
import com.hrms.backend.dto.PropertyResponse;
import com.hrms.backend.entity.Branch;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.entity.Property;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.BranchRepository;
import com.hrms.backend.repository.OrganizationRepository;
import com.hrms.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class PropertyService {
    private final PropertyRepository properties;
    private final OrganizationRepository organizations;
    private final BranchRepository branches;
    private final ScopeAuthorizationService authorization;

    public PropertyService(PropertyRepository properties, OrganizationRepository organizations, BranchRepository branches,
                           ScopeAuthorizationService authorization) {
        this.properties = properties;
        this.organizations = organizations;
        this.branches = branches;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> findAll(UUID organizationId, UUID branchId) {
        ScopeVisibility visibility = authorization.visibility("PROPERTY_READ");
        java.util.Map<UUID, Property> candidates = new java.util.LinkedHashMap<>();
        if (visibility.systemWide()) {
            List<Property> found = branchId != null ? properties.findByBranchId(branchId)
                    : organizationId != null ? properties.findByOrganizationId(organizationId) : properties.findAll();
            found.forEach(property -> candidates.put(property.getId(), property));
        } else {
            if (!visibility.organizationIds().isEmpty()) properties.findByOrganizationIdIn(visibility.organizationIds()).forEach(p -> candidates.put(p.getId(), p));
            if (!visibility.branchIds().isEmpty()) properties.findByBranchIdIn(visibility.branchIds()).forEach(p -> candidates.put(p.getId(), p));
            if (!visibility.propertyIds().isEmpty()) properties.findByIdIn(visibility.propertyIds()).forEach(p -> candidates.put(p.getId(), p));
        }
        return candidates.values().stream()
                .filter(p -> organizationId == null || p.getOrganization().getId().equals(organizationId))
                .filter(p -> branchId == null || p.getBranch().getId().equals(branchId))
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PropertyResponse findById(UUID id) {
        Property property = requireProperty(id);
        authorization.requireCapability("PROPERTY_READ", property.getOrganization().getId(), property.getBranch().getId(), property.getId(), null);
        return toResponse(property);
    }

    public PropertyResponse create(PropertyRequest request) {
        authorization.requireCapability("PROPERTY_CREATE", request.getOrganizationId(), request.getBranchId(), null, null);
        Organization organization = requireOrganization(request.getOrganizationId());
        Branch branch = requireBranch(request.getBranchId());
        validateOwnership(organization, branch);
        if (properties.existsByBranchIdAndCodeIgnoreCase(branch.getId(), normalizeCode(request.getCode()))) {
            throw new DuplicateResourceException("Property code already exists for this branch");
        }
        Property property = new Property();
        property.setOrganization(organization);
        property.setBranch(branch);
        apply(property, request, branch);
        if (request.getActive() != null) property.setActive(request.getActive());
        return toResponse(properties.save(property));
    }

    public PropertyResponse update(UUID id, PropertyRequest request) {
        Property property = requireProperty(id);
        authorization.requireCapability("PROPERTY_UPDATE", property.getOrganization().getId(), property.getBranch().getId(), property.getId(), null);
        Organization organization = requireOrganization(request.getOrganizationId());
        Branch branch = requireBranch(request.getBranchId());
        validateOwnership(organization, branch);
        UUID targetPropertyId = organization.getId().equals(property.getOrganization().getId())
                && branch.getId().equals(property.getBranch().getId()) ? property.getId() : null;
        authorization.requireCapability("PROPERTY_UPDATE", organization.getId(), branch.getId(), targetPropertyId, null);
        if (properties.existsByBranchIdAndCodeIgnoreCaseAndIdNot(branch.getId(), normalizeCode(request.getCode()), id)) {
            throw new DuplicateResourceException("Property code already exists for this branch");
        }
        property.setOrganization(organization);
        property.setBranch(branch);
        apply(property, request, branch);
        if (request.getActive() != null) property.setActive(request.getActive());
        return toResponse(properties.save(property));
    }

    public PropertyResponse setActive(UUID id, boolean active) {
        Property property = requireProperty(id);
        authorization.requireCapability("PROPERTY_UPDATE", property.getOrganization().getId(), property.getBranch().getId(), property.getId(), null);
        property.setActive(active);
        return toResponse(properties.save(property));
    }

    public void delete(UUID id) {
        Property property = requireProperty(id);
        authorization.requireCapability("PROPERTY_DELETE", property.getOrganization().getId(), property.getBranch().getId(), property.getId(), null);
        properties.delete(property);
    }

    private void validateOwnership(Organization organization, Branch branch) {
        if (!branch.getOrganization().getId().equals(organization.getId())) {
            throw new InvalidRelationshipException("The branch does not belong to the supplied organization");
        }
    }

    private Organization requireOrganization(UUID id) {
        return organizations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }

    private Branch requireBranch(UUID id) {
        return branches.findById(id).orElseThrow(() -> new ResourceNotFoundException("Branch", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private String normalizeCode(String code) { return code.trim().toUpperCase(Locale.ROOT); }

    private void apply(Property p, PropertyRequest r, Branch b) {
        p.setName(r.getName().trim());
        p.setCode(normalizeCode(r.getCode()));
        p.setDescription(r.getDescription());
        p.setEmail(r.getEmail());
        p.setPhone(r.getPhone());
        p.setAddressLine1(r.getAddressLine1());
        p.setAddressLine2(r.getAddressLine2());
        p.setCity(r.getCity());
        p.setCounty(r.getCounty());
        p.setCountry(r.getCountry() == null ? b.getCountry() : r.getCountry());
        p.setPostalCode(r.getPostalCode());
        p.setCurrencyCode(r.getCurrencyCode() == null ? b.getCurrencyCode() : r.getCurrencyCode().toUpperCase(Locale.ROOT));
        p.setTimezone(r.getTimezone() == null ? b.getTimezone() : r.getTimezone());
    }

    private PropertyResponse toResponse(Property p) {
        Organization organization = p.getOrganization();
        Branch branch = p.getBranch();
        return PropertyResponse.builder()
                .id(p.getId()).organizationId(organization.getId()).organizationName(organization.getName())
                .branchId(branch.getId()).branchName(branch.getName()).name(p.getName()).code(p.getCode())
                .description(p.getDescription()).email(p.getEmail()).phone(p.getPhone())
                .addressLine1(p.getAddressLine1()).addressLine2(p.getAddressLine2()).city(p.getCity()).county(p.getCounty())
                .country(p.getCountry()).postalCode(p.getPostalCode()).currencyCode(p.getCurrencyCode().trim())
                .timezone(p.getTimezone()).active(p.getActive()).createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt()).build();
    }
}
