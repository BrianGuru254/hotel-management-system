package com.hrms.backend.service;

import com.hrms.backend.dto.OrganizationRequest;
import com.hrms.backend.dto.OrganizationResponse;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizations;

    public OrganizationService(OrganizationRepository organizations) { this.organizations = organizations; }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() { return organizations.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(UUID id) { return toResponse(get(id)); }

    public OrganizationResponse create(OrganizationRequest request) {
        if (organizations.existsByNameIgnoreCase(request.getName().trim()))
            throw new DuplicateResourceException("Organization name already exists");
        Organization organization = new Organization();
        apply(organization, request);
        if (request.getActive() != null) organization.setActive(request.getActive());
        return toResponse(organizations.save(organization));
    }

    public OrganizationResponse update(UUID id, OrganizationRequest request) {
        Organization organization = get(id);
        if (organizations.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id))
            throw new DuplicateResourceException("Organization name already exists");
        apply(organization, request);
        if (request.getActive() != null) organization.setActive(request.getActive());
        return toResponse(organizations.save(organization));
    }

    public OrganizationResponse setActive(UUID id, boolean active) {
        Organization organization = get(id);
        organization.setActive(active);
        return toResponse(organizations.save(organization));
    }

    public void delete(UUID id) { organizations.delete(get(id)); }

    private Organization get(UUID id) { return organizations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization", id)); }

    private void apply(Organization o, OrganizationRequest r) {
        o.setName(r.getName().trim()); o.setLegalName(r.getLegalName()); o.setRegistrationNumber(r.getRegistrationNumber());
        o.setTaxNumber(r.getTaxNumber()); o.setEmail(r.getEmail()); o.setPhone(r.getPhone()); o.setWebsite(r.getWebsite());
        o.setAddressLine1(r.getAddressLine1()); o.setAddressLine2(r.getAddressLine2()); o.setCity(r.getCity());
        o.setCounty(r.getCounty()); o.setCountry(r.getCountry() == null ? "Kenya" : r.getCountry()); o.setPostalCode(r.getPostalCode());
        o.setCurrencyCode(r.getCurrencyCode() == null ? "KES" : r.getCurrencyCode().toUpperCase(Locale.ROOT));
        o.setTimezone(r.getTimezone() == null ? "Africa/Nairobi" : r.getTimezone());
    }

    private OrganizationResponse toResponse(Organization o) {
        return OrganizationResponse.builder().id(o.getId()).name(o.getName()).legalName(o.getLegalName()).registrationNumber(o.getRegistrationNumber())
                .taxNumber(o.getTaxNumber()).email(o.getEmail()).phone(o.getPhone()).website(o.getWebsite()).addressLine1(o.getAddressLine1())
                .addressLine2(o.getAddressLine2()).city(o.getCity()).county(o.getCounty()).country(o.getCountry()).postalCode(o.getPostalCode())
                .currencyCode(o.getCurrencyCode().trim()).timezone(o.getTimezone()).active(o.getActive()).createdAt(o.getCreatedAt()).updatedAt(o.getUpdatedAt()).build();
    }
}
