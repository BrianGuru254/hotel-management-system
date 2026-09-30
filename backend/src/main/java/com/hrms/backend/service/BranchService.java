package com.hrms.backend.service;

import com.hrms.backend.dto.BranchRequest;
import com.hrms.backend.dto.BranchResponse;
import com.hrms.backend.entity.Branch;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.BranchRepository;
import com.hrms.backend.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class BranchService {
    private final BranchRepository branches;
    private final OrganizationRepository organizations;

    public BranchService(BranchRepository branches, OrganizationRepository organizations) { this.branches = branches; this.organizations = organizations; }

    @Transactional(readOnly = true)
    public List<BranchResponse> findAll(UUID organizationId) {
        if (organizationId == null) return branches.findAll().stream().map(this::toResponse).toList();
        requireOrganization(organizationId);
        return branches.findByOrganizationId(organizationId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse findById(UUID id) { return toResponse(get(id)); }

    public BranchResponse create(BranchRequest request) {
        Organization organization = requireOrganization(request.getOrganizationId());
        if (branches.existsByOrganizationIdAndCodeIgnoreCase(organization.getId(), request.getCode().trim()))
            throw new DuplicateResourceException("Branch code already exists for this organization");
        Branch branch = new Branch(); branch.setOrganization(organization); apply(branch, request);
        if (request.getActive() != null) branch.setActive(request.getActive());
        return toResponse(branches.save(branch));
    }

    public BranchResponse update(UUID id, BranchRequest request) {
        Branch branch = get(id);
        Organization organization = requireOrganization(request.getOrganizationId());
        if (branches.existsByOrganizationIdAndCodeIgnoreCaseAndIdNot(organization.getId(), request.getCode().trim(), id))
            throw new DuplicateResourceException("Branch code already exists for this organization");
        branch.setOrganization(organization); apply(branch, request);
        if (request.getActive() != null) branch.setActive(request.getActive());
        return toResponse(branches.save(branch));
    }

    public BranchResponse setActive(UUID id, boolean active) { Branch branch = get(id); branch.setActive(active); return toResponse(branches.save(branch)); }
    public void delete(UUID id) { branches.delete(get(id)); }

    private Branch get(UUID id) { return branches.findById(id).orElseThrow(() -> new ResourceNotFoundException("Branch", id)); }
    private Organization requireOrganization(UUID id) { return organizations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization", id)); }

    private void apply(Branch b, BranchRequest r) {
        b.setName(r.getName().trim()); b.setCode(r.getCode().trim()); b.setDescription(r.getDescription()); b.setEmail(r.getEmail()); b.setPhone(r.getPhone());
        b.setAddressLine1(r.getAddressLine1()); b.setAddressLine2(r.getAddressLine2()); b.setCity(r.getCity()); b.setCounty(r.getCounty());
        b.setCountry(r.getCountry() == null ? "Kenya" : r.getCountry()); b.setPostalCode(r.getPostalCode());
        b.setCurrencyCode(r.getCurrencyCode() == null ? "KES" : r.getCurrencyCode().toUpperCase(Locale.ROOT));
        b.setTimezone(r.getTimezone() == null ? "Africa/Nairobi" : r.getTimezone());
    }

    private BranchResponse toResponse(Branch b) {
        return BranchResponse.builder().id(b.getId()).organizationId(b.getOrganization().getId()).organizationName(b.getOrganization().getName())
                .name(b.getName()).code(b.getCode()).description(b.getDescription()).email(b.getEmail()).phone(b.getPhone())
                .addressLine1(b.getAddressLine1()).addressLine2(b.getAddressLine2()).city(b.getCity()).county(b.getCounty()).country(b.getCountry())
                .postalCode(b.getPostalCode()).currencyCode(b.getCurrencyCode().trim()).timezone(b.getTimezone()).active(b.getActive())
                .createdAt(b.getCreatedAt()).updatedAt(b.getUpdatedAt()).build();
    }
}
