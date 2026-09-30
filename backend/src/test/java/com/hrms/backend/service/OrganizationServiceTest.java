package com.hrms.backend.service;

import com.hrms.backend.dto.OrganizationRequest;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {
    @Mock OrganizationRepository repository;
    @InjectMocks OrganizationService service;

    @Test void createsOrganizationWithDefaults() {
        OrganizationRequest request = new OrganizationRequest(); request.setName("  Acme Hotel  ");
        when(repository.existsByNameIgnoreCase("Acme Hotel")).thenReturn(false);
        when(repository.save(any(Organization.class))).thenAnswer(call -> {
            Organization saved = call.getArgument(0); saved.setId(java.util.UUID.randomUUID()); return saved;
        });
        var result = service.create(request);
        assertEquals("Acme Hotel", result.getName());
        assertEquals("KES", result.getCurrencyCode());
        assertTrue(result.getActive());
    }

    @Test void rejectsDuplicateName() {
        OrganizationRequest request = new OrganizationRequest(); request.setName("Existing");
        when(repository.existsByNameIgnoreCase("Existing")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }
}
