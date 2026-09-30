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
    private final ScopeAuthorizationService authorization;

    public BranchService(BranchRepository branches, OrganizationRepository organizations, ScopeAuthorizationService authorization) {
        this.branches = branches;
        this.organizations = organizations;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findAll(UUID organizationId) {
        ScopeVisibility visibility = authorization.visibility("BRANCH_READ");
        List<Branch> visible;
        if (visibility.systemWide()) visible = organizationId == null ? branches.findAll() : branches.findByOrganizationId(organizationId);
        else {
            List<Branch> scoped = new java.util.ArrayList<>();
            if (!visibility.organizationIds().isEmpty()) scoped.addAll(branches.findByOrganizationIdIn(visibility.organizationIds()));
            if (!visibility.branchIds().isEmpty()) scoped.addAll(branches.findByIdIn(visibility.branchIds()));
            java.util.Map<UUID, Branch> distinct = new java.util.LinkedHashMap<>();
            scoped.stream().filter(branch -> organizationId == null || branch.getOrganization().getId().equals(organizationId))
                    .forEach(branch -> distinct.put(branch.getId(), branch));
            visible = List.copyOf(distinct.values());
        }
        return visible.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse findById(UUID id) {
        Branch branch = get(id);
        authorization.requireCapability("BRANCH_READ", branch.getOrganization().getId(), branch.getId(), null, null);
        return toResponse(branch);
    }

    public BranchResponse create(BranchRequest request) {
        Organization organization = requireOrganization(request.getOrganizationId());
        authorization.requireCapability("BRANCH_CREATE", organization.getId(), null, null, null);
        if (branches.existsByOrganizationIdAndCodeIgnoreCase(organization.getId(), request.getCode().trim()))
            throw new DuplicateResourceException("Branch code already exists for this organization");
        Branch branch = new Branch(); branch.setOrganization(organization); apply(branch, request);
        if (request.getActive() != null) branch.setActive(request.getActive());
        return toResponse(branches.save(branch));
    }

    public BranchResponse update(UUID id, BranchRequest request) {
        Branch branch = get(id);
        authorization.requireCapability("BRANCH_UPDATE", branch.getOrganization().getId(), branch.getId(), null, null);
        Organization organization = requireOrganization(request.getOrganizationId());
        if (!branch.getOrganization().getId().equals(organization.getId())) {
            authorization.requireCapability("BRANCH_CREATE", organization.getId(), null, null, null);
        }
        if (branches.existsByOrganizationIdAndCodeIgnoreCaseAndIdNot(organization.getId(), request.getCode().trim(), id))
            throw new DuplicateResourceException("Branch code already exists for this organization");
        branch.setOrganization(organization); apply(branch, request);
        if (request.getActive() != null) branch.setActive(request.getActive());
        return toResponse(branches.save(branch));
    }

    public BranchResponse setActive(UUID id, boolean active) {
        Branch branch = get(id);
        authorization.requireCapability("BRANCH_UPDATE", branch.getOrganization().getId(), branch.getId(), null, null);
        branch.setActive(active);
        return toResponse(branches.save(branch));
    }
    public void delete(UUID id) {
        Branch branch = get(id);
        authorization.requireCapability("BRANCH_DELETE", branch.getOrganization().getId(), branch.getId(), null, null);
        branches.delete(branch);
    }

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
