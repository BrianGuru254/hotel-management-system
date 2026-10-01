package com.hrms.backend.service;

import com.hrms.backend.dto.GuestCreateRequest;
import com.hrms.backend.entity.Branch;
import com.hrms.backend.entity.Guest;
import com.hrms.backend.entity.Organization;
import com.hrms.backend.entity.Property;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.repository.GuestRepository;
import com.hrms.backend.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {
    @Mock GuestRepository guests;
    @Mock PropertyRepository properties;
    @Mock ScopeAuthorizationService authorization;
    @InjectMocks GuestService service;

    @Test
    void createsGuestInTheAuthorizedPropertyAndNormalizesContactAndIdentityData() {
        Property property = property();
        when(properties.findById(property.getId())).thenReturn(Optional.of(property));
        when(guests.existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCase(
                property.getId(), "PASSPORT", "AB12345")).thenReturn(false);
        when(guests.save(any())).thenAnswer(call -> {
            Guest saved = call.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setCreatedAt(OffsetDateTime.now());
            saved.setUpdatedAt(saved.getCreatedAt());
            return saved;
        });

        var response = service.create(request(property.getId()));

        assertEquals(property.getId(), response.propertyId());
        assertTrue(response.guestNumber().startsWith("G-"));
        assertEquals("Alice", response.firstName());
        assertEquals("alice@example.test", response.email());
        assertEquals("PASSPORT", response.identificationType());
        assertEquals("AB12345", response.identificationNumber());
        verify(authorization).requireCapability("GUEST_CREATE", property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    @Test
    void rejectsDuplicateIdentificationWithinTheSameProperty() {
        Property property = property();
        when(properties.findById(property.getId())).thenReturn(Optional.of(property));
        when(guests.existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCase(
                property.getId(), "PASSPORT", "AB12345")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request(property.getId())));
        verify(guests, never()).save(any());
    }

    @Test
    void refusesGuestCreationOutsideTheAssignedPropertyScope() {
        Property property = property();
        when(properties.findById(property.getId())).thenReturn(Optional.of(property));
        doThrow(new AccessDeniedException("outside scope"))
                .when(authorization).requireCapability("GUEST_CREATE", property.getOrganization().getId(),
                        property.getBranch().getId(), property.getId(), null);

        assertThrows(AccessDeniedException.class, () -> service.create(request(property.getId())));
        verify(guests, never()).save(any());
    }

    private GuestCreateRequest request(UUID propertyId) {
        return new GuestCreateRequest(propertyId, " Alice ", null, " Guest ", " +254700000000 ",
                " Alice@Example.Test ", " Kenyan ", " passport ", " ab12345 ", null, null,
                null, null, null, null, null, null, null);
    }

    private Property property() {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID());
        branch.setOrganization(organization);
        Property property = new Property();
        property.setId(UUID.randomUUID());
        property.setOrganization(organization);
        property.setBranch(branch);
        return property;
    }
}
