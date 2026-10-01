package com.hrms.backend.service;

import com.hrms.backend.dto.RoomTypeCreateRequest;
import com.hrms.backend.dto.RoomTypeResponse;
import com.hrms.backend.dto.RoomTypeUpdateRequest;
import com.hrms.backend.entity.Property;
import com.hrms.backend.entity.RoomType;
import com.hrms.backend.entity.RoomTypeStatus;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.PropertyRepository;
import com.hrms.backend.repository.RoomTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class RoomTypeService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of("name", "code", "basePrice", "maxOccupancy", "createdAt");

    private final RoomTypeRepository roomTypes;
    private final PropertyRepository properties;
    private final PropertyVisibilityService propertyVisibility;
    private final ScopeAuthorizationService authorization;

    public RoomTypeService(RoomTypeRepository roomTypes, PropertyRepository properties,
                           PropertyVisibilityService propertyVisibility, ScopeAuthorizationService authorization) {
        this.roomTypes = roomTypes;
        this.properties = properties;
        this.propertyVisibility = propertyVisibility;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<RoomTypeResponse> findAll(UUID propertyId, String name, String code, RoomTypeStatus status,
                                          int page, int size, String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility visibility = authorization.visibility("ROOM_TYPE_READ");
        Set<UUID> ids = propertyVisibility.visiblePropertyIds(visibility, propertyId);
        if (ids.isEmpty()) return Page.empty(pageable);
        return roomTypes.searchInProperties(ids, propertyId, normalizeOptional(name), normalizeOptional(code),
                        status, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public RoomTypeResponse findById(UUID id) {
        RoomType roomType = requireRoomType(id);
        authorize("ROOM_TYPE_READ", roomType.getProperty());
        return toResponse(roomType);
    }

    public RoomTypeResponse create(RoomTypeCreateRequest request) {
        Property property = requireProperty(request.propertyId());
        authorize("ROOM_TYPE_CREATE", property);
        String code = normalizeCode(request.code());
        if (roomTypes.existsByPropertyIdAndCodeIgnoreCase(property.getId(), code)) {
            throw new DuplicateResourceException("Room type code already exists for this property");
        }
        RoomType roomType = new RoomType();
        roomType.setProperty(property);
        roomType.setName(request.name().trim());
        roomType.setCode(code);
        roomType.setDescription(normalizeOptional(request.description()));
        roomType.setBasePrice(request.basePrice() == null ? BigDecimal.ZERO : request.basePrice());
        roomType.setMaxOccupancy(request.maxOccupancy() == null ? 1 : request.maxOccupancy());
        roomType.setSizeSquareMeters(request.sizeSquareMeters());
        roomType.setStatus(request.status() == null ? RoomTypeStatus.ACTIVE : request.status());
        return toResponse(roomTypes.save(roomType));
    }

    public RoomTypeResponse update(UUID id, RoomTypeUpdateRequest request) {
        RoomType roomType = requireRoomType(id);
        authorize("ROOM_TYPE_UPDATE", roomType.getProperty());
        String code = normalizeCode(request.code());
        if (roomTypes.existsByPropertyIdAndCodeIgnoreCaseAndIdNot(roomType.getProperty().getId(), code, id)) {
            throw new DuplicateResourceException("Room type code already exists for this property");
        }
        roomType.setName(request.name().trim());
        roomType.setCode(code);
        roomType.setDescription(normalizeOptional(request.description()));
        roomType.setBasePrice(request.basePrice());
        roomType.setMaxOccupancy(request.maxOccupancy());
        roomType.setSizeSquareMeters(request.sizeSquareMeters());
        if (request.status() != null) roomType.setStatus(request.status());
        return toResponse(roomTypes.save(roomType));
    }

    public void delete(UUID id) {
        RoomType roomType = requireRoomType(id);
        authorize("ROOM_TYPE_DELETE", roomType.getProperty());
        roomTypes.delete(roomType);
    }

    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported room type sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }

    private RoomType requireRoomType(UUID id) {
        return roomTypes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private RoomTypeResponse toResponse(RoomType roomType) {
        Property property = roomType.getProperty();
        return new RoomTypeResponse(roomType.getId(), property.getId(), property.getName(),
                property.getCurrencyCode().trim(), roomType.getName(), roomType.getCode(), roomType.getDescription(),
                roomType.getBasePrice(), roomType.getMaxOccupancy(), roomType.getSizeSquareMeters(),
                roomType.getStatus(), roomType.getCreatedAt(), roomType.getUpdatedAt());
    }
}
