package com.hrms.backend.service;

import com.hrms.backend.dto.BranchRequest;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.BranchRepository;
import com.hrms.backend.repository.OrganizationRepository;
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
class BranchServiceTest {
    @Mock BranchRepository branches;
    @Mock OrganizationRepository organizations;
    @InjectMocks BranchService service;

    @Test void rejectsUnknownOrganization() {
        UUID id = UUID.randomUUID();
        BranchRequest request = new BranchRequest(); request.setOrganizationId(id); request.setName("Downtown"); request.setCode("DT");
        when(organizations.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
        verify(branches, never()).save(any());
    }

    @Test void updateRequiresAnExistingOrganization() {
        UUID organizationId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        BranchRequest request = new BranchRequest(); request.setOrganizationId(organizationId); request.setName("Downtown"); request.setCode("DT");
        when(branches.findById(branchId)).thenReturn(Optional.of(new com.hrms.backend.entity.Branch()));
        when(organizations.findById(organizationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(branchId, request));
        verify(branches, never()).save(any());
    }

    @Test void rejectsDuplicateBranchCode() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization(); organization.setId(organizationId);
        BranchRequest request = new BranchRequest(); request.setOrganizationId(organizationId); request.setName("Downtown"); request.setCode("DT");
        when(organizations.findById(organizationId)).thenReturn(Optional.of(organization));
        when(branches.existsByOrganizationIdAndCodeIgnoreCase(organizationId, "DT")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(branches, never()).save(any());
    }

    @Test void createsBranchAttachedToOrganization() {
        UUID id = UUID.randomUUID();
        Organization organization = new Organization(); organization.setId(id); organization.setName("Hotel Group");
        BranchRequest request = new BranchRequest(); request.setOrganizationId(id); request.setName("Downtown"); request.setCode("DT");
        when(organizations.findById(id)).thenReturn(Optional.of(organization));
        when(branches.existsByOrganizationIdAndCodeIgnoreCase(id, "DT")).thenReturn(false);
        when(branches.save(any())).thenAnswer(call -> { var b = (com.hrms.backend.entity.Branch) call.getArgument(0); b.setId(UUID.randomUUID()); return b; });
        var result = service.create(request);
        assertEquals(id, result.getOrganizationId()); assertEquals("Hotel Group", result.getOrganizationName());
    }
}
