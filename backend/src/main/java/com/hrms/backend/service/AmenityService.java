package com.hrms.backend.service;

import com.hrms.backend.dto.AmenityCreateRequest;
import com.hrms.backend.dto.AmenityResponse;
import com.hrms.backend.dto.AmenityUpdateRequest;
import com.hrms.backend.entity.Amenity;
import com.hrms.backend.entity.Property;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.AmenityRepository;
import com.hrms.backend.repository.PropertyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AmenityService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of("name", "createdAt");

    private final AmenityRepository amenities;
    private final PropertyRepository properties;
    private final PropertyVisibilityService propertyVisibility;
    private final ScopeAuthorizationService authorization;

    public AmenityService(AmenityRepository amenities, PropertyRepository properties,
                          PropertyVisibilityService propertyVisibility, ScopeAuthorizationService authorization) {
        this.amenities = amenities;
        this.properties = properties;
        this.propertyVisibility = propertyVisibility;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<AmenityResponse> findAll(UUID propertyId, String name, int page, int size,
                                         String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility visibility = authorization.visibility("AMENITY_READ");
        Set<UUID> ids = propertyVisibility.visiblePropertyIds(visibility, propertyId);
        if (ids.isEmpty()) return Page.empty(pageable);
        return amenities.searchInProperties(ids, propertyId, normalizeOptional(name), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AmenityResponse findById(UUID id) {
        Amenity amenity = requireAmenity(id);
        authorize("AMENITY_READ", amenity.getProperty());
        return toResponse(amenity);
    }

    public AmenityResponse create(AmenityCreateRequest request) {
        Property property = requireProperty(request.propertyId());
        authorize("AMENITY_CREATE", property);
        String name = normalizeName(request.name());
        if (amenities.existsByPropertyIdAndNameIgnoreCase(property.getId(), name)) {
            throw new DuplicateResourceException("Amenity name already exists for this property");
        }
        Amenity amenity = new Amenity();
        amenity.setProperty(property);
        amenity.setName(name);
        amenity.setDescription(normalizeOptional(request.description()));
        return toResponse(amenities.save(amenity));
    }

    public AmenityResponse update(UUID id, AmenityUpdateRequest request) {
        Amenity amenity = requireAmenity(id);
        authorize("AMENITY_UPDATE", amenity.getProperty());
        String name = normalizeName(request.name());
        if (amenities.existsByPropertyIdAndNameIgnoreCaseAndIdNot(amenity.getProperty().getId(), name, id)) {
            throw new DuplicateResourceException("Amenity name already exists for this property");
        }
        amenity.setName(name);
        amenity.setDescription(normalizeOptional(request.description()));
        return toResponse(amenities.save(amenity));
    }

    public void delete(UUID id) {
        Amenity amenity = requireAmenity(id);
        authorize("AMENITY_DELETE", amenity.getProperty());
        amenities.delete(amenity);
    }

    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported amenity sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }

    private Amenity requireAmenity(UUID id) {
        return amenities.findById(id).orElseThrow(() -> new ResourceNotFoundException("Amenity", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private AmenityResponse toResponse(Amenity amenity) {
        Property property = amenity.getProperty();
        return new AmenityResponse(amenity.getId(), property.getId(), property.getName(),
                amenity.getName(), amenity.getDescription(), amenity.getCreatedAt(), amenity.getUpdatedAt());
    }
}
