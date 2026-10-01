package com.hrms.backend.service;

import com.hrms.backend.dto.GuestCreateRequest;
import com.hrms.backend.dto.GuestResponse;
import com.hrms.backend.dto.GuestUpdateRequest;
import com.hrms.backend.entity.Guest;
import com.hrms.backend.entity.Property;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.GuestRepository;
import com.hrms.backend.repository.PropertyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class GuestService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "guestNumber", "firstName", "lastName", "email", "createdAt");

    private final GuestRepository guests;
    private final PropertyRepository properties;
    private final ScopeAuthorizationService authorization;

    public GuestService(GuestRepository guests, PropertyRepository properties,
                        ScopeAuthorizationService authorization) {
        this.guests = guests;
        this.properties = properties;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<GuestResponse> findAll(UUID propertyId, String name, String phone, String email,
                                       String guestNumber, String identificationNumber,
                                       int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported guest sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        ScopeVisibility visibility = authorization.visibility("GUEST_READ");
        Set<UUID> accessiblePropertyIds = accessiblePropertyIds(visibility, propertyId);
        if (accessiblePropertyIds.isEmpty()) return Page.empty(pageable);

        return guests.searchInProperties(accessiblePropertyIds, propertyId,
                        normalizeSearch(name), normalizeSearch(phone), normalizeSearch(email),
                        normalizeSearch(guestNumber), normalizeSearch(identificationNumber), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public GuestResponse findById(UUID id) {
        Guest guest = requireGuest(id);
        authorize("GUEST_READ", guest.getProperty());
        return toResponse(guest);
    }

    public GuestResponse create(GuestCreateRequest request) {
        Property property = requireProperty(request.propertyId());
        authorize("GUEST_CREATE", property);
        String identificationType = normalizeIdentifier(request.identificationType());
        String identificationNumber = normalizeIdentifier(request.identificationNumber());
        if (identificationType != null && identificationNumber != null
                && guests.existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCase(
                property.getId(), identificationType, identificationNumber)) {
            throw new DuplicateResourceException("Identification number already exists for this property");
        }

        Guest guest = new Guest();
        guest.setProperty(property);
        guest.setGuestNumber("G-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT));
        apply(guest, request.firstName(), request.middleName(), request.lastName(), request.phone(), request.email(),
                request.nationality(), identificationType, identificationNumber, request.addressLine1(),
                request.addressLine2(), request.city(), request.county(), request.country(), request.dateOfBirth(),
                request.emergencyContactName(), request.emergencyContactPhone(), request.notes());
        return toResponse(guests.save(guest));
    }

    public GuestResponse update(UUID id, GuestUpdateRequest request) {
        Guest guest = requireGuest(id);
        authorize("GUEST_UPDATE", guest.getProperty());
        String identificationType = normalizeIdentifier(request.identificationType());
        String identificationNumber = normalizeIdentifier(request.identificationNumber());
        if (identificationType != null && identificationNumber != null
                && guests.existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCaseAndIdNot(
                guest.getProperty().getId(), identificationType, identificationNumber, guest.getId())) {
            throw new DuplicateResourceException("Identification number already exists for this property");
        }

        apply(guest, request.firstName(), request.middleName(), request.lastName(), request.phone(), request.email(),
                request.nationality(), identificationType, identificationNumber, request.addressLine1(),
                request.addressLine2(), request.city(), request.county(), request.country(), request.dateOfBirth(),
                request.emergencyContactName(), request.emergencyContactPhone(), request.notes());
        return toResponse(guests.save(guest));
    }

    private Set<UUID> accessiblePropertyIds(ScopeVisibility visibility, UUID requestedPropertyId) {
        Set<UUID> ids = new LinkedHashSet<>();
        if (visibility.systemWide()) {
            if (requestedPropertyId != null) return Set.of(requestedPropertyId);
            properties.findAll().forEach(property -> ids.add(property.getId()));
            return ids;
        }
        if (!visibility.organizationIds().isEmpty()) {
            properties.findByOrganizationIdIn(visibility.organizationIds()).forEach(property -> ids.add(property.getId()));
        }
        if (!visibility.branchIds().isEmpty()) {
            properties.findByBranchIdIn(visibility.branchIds()).forEach(property -> ids.add(property.getId()));
        }
        if (!visibility.propertyIds().isEmpty()) {
            properties.findByIdIn(visibility.propertyIds()).forEach(property -> ids.add(property.getId()));
        }
        if (requestedPropertyId != null && !ids.contains(requestedPropertyId)) return Set.of();
        return ids;
    }

    private Guest requireGuest(UUID id) {
        return guests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private void apply(Guest guest, String firstName, String middleName, String lastName, String phone,
                       String email, String nationality, String identificationType, String identificationNumber,
                       String addressLine1, String addressLine2, String city, String county, String country,
                       java.time.LocalDate dateOfBirth, String emergencyContactName, String emergencyContactPhone,
                       String notes) {
        guest.setFirstName(firstName.trim());
        guest.setMiddleName(normalizeOptional(middleName));
        guest.setLastName(lastName.trim());
        guest.setPhone(normalizeOptional(phone));
        guest.setEmail(normalizeEmail(email));
        guest.setNationality(normalizeOptional(nationality));
        guest.setIdentificationType(identificationType);
        guest.setIdentificationNumber(identificationNumber);
        guest.setAddressLine1(normalizeOptional(addressLine1));
        guest.setAddressLine2(normalizeOptional(addressLine2));
        guest.setCity(normalizeOptional(city));
        guest.setCounty(normalizeOptional(county));
        guest.setCountry(normalizeOptional(country));
        guest.setDateOfBirth(dateOfBirth);
        guest.setEmergencyContactName(normalizeOptional(emergencyContactName));
        guest.setEmergencyContactPhone(normalizeOptional(emergencyContactPhone));
        guest.setNotes(normalizeOptional(notes));
    }

    private GuestResponse toResponse(Guest guest) {
        return new GuestResponse(guest.getId(), guest.getProperty().getId(), guest.getGuestNumber(),
                guest.getFirstName(), guest.getMiddleName(), guest.getLastName(), guest.getPhone(), guest.getEmail(),
                guest.getNationality(), guest.getIdentificationType(), guest.getIdentificationNumber(),
                guest.getAddressLine1(), guest.getAddressLine2(), guest.getCity(), guest.getCounty(),
                guest.getCountry(), guest.getDateOfBirth(), guest.getEmergencyContactName(),
                guest.getEmergencyContactPhone(), guest.getNotes(), guest.getCreatedAt(), guest.getUpdatedAt());
    }

    private String normalizeEmail(String email) {
        String normalized = normalizeOptional(email);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private String normalizeIdentifier(String value) {
        String normalized = normalizeOptional(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private String normalizeSearch(String value) {
        return normalizeOptional(value);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
