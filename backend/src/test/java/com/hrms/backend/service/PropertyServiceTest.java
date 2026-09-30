package com.hrms.backend.service;

import com.hrms.backend.dto.PropertyRequest;
import com.hrms.backend.entity.Branch;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.entity.Property;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.repository.BranchRepository;
import com.hrms.backend.repository.OrganizationRepository;
import com.hrms.backend.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {
    @Mock PropertyRepository properties;
    @Mock OrganizationRepository organizations;
    @Mock BranchRepository branches;
    @Mock ScopeAuthorizationService authorization;
    @InjectMocks PropertyService service;

    @Test void createsPropertyWithinItsOrganizationsBranch() {
        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        Organization organization = organization(organizationId, "Hotel Group");
        Branch branch = branch(branchId, organization);
        PropertyRequest request = request(organizationId, branchId);
        when(organizations.findById(organizationId)).thenReturn(Optional.of(organization));
        when(branches.findById(branchId)).thenReturn(Optional.of(branch));
        when(properties.existsByBranchIdAndCodeIgnoreCase(branchId, "H1")).thenReturn(false);
        when(properties.save(any())).thenAnswer(call -> {
            Property saved = call.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        var result = service.create(request);

        assertEquals(organizationId, result.getOrganizationId());
        assertEquals(branchId, result.getBranchId());
        assertEquals("KES", result.getCurrencyCode());
    }

    @Test void rejectsPropertyWhoseOrganizationDoesNotOwnItsBranch() {
        Organization requestedOrganization = organization(UUID.randomUUID(), "Group A");
        Organization branchOrganization = organization(UUID.randomUUID(), "Group B");
        Branch branch = branch(UUID.randomUUID(), branchOrganization);
        PropertyRequest request = request(requestedOrganization.getId(), branch.getId());
        when(organizations.findById(requestedOrganization.getId())).thenReturn(Optional.of(requestedOrganization));
        when(branches.findById(branch.getId())).thenReturn(Optional.of(branch));

        assertThrows(InvalidRelationshipException.class, () -> service.create(request));
        verify(properties, never()).save(any());
    }

    @Test void rejectsMismatchedOrganizationWhenUpdatingProperty() {
        Organization requestedOrganization = organization(UUID.randomUUID(), "Group A");
        Organization branchOrganization = organization(UUID.randomUUID(), "Group B");
        Branch branch = branch(UUID.randomUUID(), branchOrganization);
        Property existing = new Property(); existing.setId(UUID.randomUUID());
        existing.setOrganization(organization(UUID.randomUUID(), "Existing Group"));
        existing.setBranch(branch(UUID.randomUUID(), existing.getOrganization()));
        when(properties.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(organizations.findById(requestedOrganization.getId())).thenReturn(Optional.of(requestedOrganization));
        when(branches.findById(branch.getId())).thenReturn(Optional.of(branch));

        assertThrows(InvalidRelationshipException.class, () -> service.update(existing.getId(), request(requestedOrganization.getId(), branch.getId())));
        verify(properties, never()).save(any());
    }

    private PropertyRequest request(UUID organizationId, UUID branchId) {
        PropertyRequest request = new PropertyRequest();
        request.setOrganizationId(organizationId); request.setBranchId(branchId);
        request.setName("Riverside Hotel"); request.setCode("H1");
        return request;
    }

    private Organization organization(UUID id, String name) {
        Organization organization = new Organization(); organization.setId(id); organization.setName(name);
        organization.setCountry("Kenya"); organization.setCurrencyCode("KES"); organization.setTimezone("Africa/Nairobi");
        return organization;
    }

    private Branch branch(UUID id, Organization organization) {
        Branch branch = new Branch(); branch.setId(id); branch.setOrganization(organization); branch.setName("Main Branch");
        branch.setCountry("Kenya"); branch.setCurrencyCode("KES"); branch.setTimezone("Africa/Nairobi");
        return branch;
    }
}
