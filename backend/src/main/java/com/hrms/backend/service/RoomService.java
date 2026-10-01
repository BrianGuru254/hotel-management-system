package com.hrms.backend.service;

import com.hrms.backend.dto.RoomAmenitiesRequest;
import com.hrms.backend.dto.RoomCreateRequest;
import com.hrms.backend.dto.RoomResponse;
import com.hrms.backend.dto.RoomStatusChangeRequest;
import com.hrms.backend.dto.RoomUpdateRequest;
import com.hrms.backend.entity.Amenity;
import com.hrms.backend.entity.Property;
import com.hrms.backend.entity.Room;
import com.hrms.backend.entity.RoomStatus;
import com.hrms.backend.entity.RoomType;
import com.hrms.backend.entity.RoomTypeStatus;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.AmenityRepository;
import com.hrms.backend.repository.PropertyRepository;
import com.hrms.backend.repository.RoomAmenityRepository;
import com.hrms.backend.repository.RoomRepository;
import com.hrms.backend.repository.RoomTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class RoomService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of("roomNumber", "floorNumber", "capacity", "currentStatus", "createdAt");

    private final RoomRepository rooms;
    private final RoomTypeRepository roomTypes;
    private final AmenityRepository amenities;
    private final RoomAmenityRepository roomAmenities;
    private final PropertyRepository properties;
    private final PropertyVisibilityService propertyVisibility;
    private final ScopeAuthorizationService authorization;

    public RoomService(RoomRepository rooms, RoomTypeRepository roomTypes, AmenityRepository amenities,
                       RoomAmenityRepository roomAmenities, PropertyRepository properties,
                       PropertyVisibilityService propertyVisibility, ScopeAuthorizationService authorization) {
        this.rooms = rooms;
        this.roomTypes = roomTypes;
        this.amenities = amenities;
        this.roomAmenities = roomAmenities;
        this.properties = properties;
        this.propertyVisibility = propertyVisibility;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> findAll(UUID propertyId, UUID roomTypeId, String roomNumber, RoomStatus status,
                                      Integer floorNumber, int page, int size, String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility visibility = authorization.visibility("ROOM_READ");
        Set<UUID> visibleIds = propertyVisibility.visiblePropertyIds(visibility, propertyId);
        if (visibleIds.isEmpty()) return Page.empty(pageable);
        Page<Room> result = rooms.searchInProperties(visibleIds, propertyId, roomTypeId,
                normalizeOptional(roomNumber), status, floorNumber, pageable);
        Map<UUID, List<UUID>> amenityIds = roomAmenities.findAmenityIdsByRoomIds(
                result.getContent().stream().map(Room::getId).toList());
        return result.map(room -> toResponse(room, amenityIds.getOrDefault(room.getId(), List.of())));
    }

    @Transactional(readOnly = true)
    public RoomResponse findById(UUID id) {
        Room room = requireRoom(id);
        authorize("ROOM_READ", room.getProperty());
        return toResponse(room, roomAmenities.findAmenityIdsByRoomId(room.getId()));
    }

    public RoomResponse create(RoomCreateRequest request) {
        Property property = requireProperty(request.propertyId());
        authorize("ROOM_CREATE", property);
        RoomType roomType = requireRoomType(request.roomTypeId());
        validateRoomType(property, roomType);
        Integer capacity = request.capacity() == null ? roomType.getMaxOccupancy() : request.capacity();
        validateCapacity(capacity, roomType);
        String roomNumber = normalizeRoomNumber(request.roomNumber());
        ensureRoomNumberAvailable(property.getId(), roomNumber, null);

        Room room = new Room();
        room.setProperty(property);
        room.setRoomType(roomType);
        room.setRoomNumber(roomNumber);
        room.setFloorNumber(request.floorNumber());
        room.setCapacity(capacity);
        room.setCurrentStatus(RoomStatus.AVAILABLE);
        room.setDescription(normalizeOptional(request.description()));
        Room saved = rooms.save(room);
        return toResponse(saved, List.of());
    }

    public RoomResponse update(UUID id, RoomUpdateRequest request) {
        Room room = requireRoom(id);
        authorize("ROOM_UPDATE", room.getProperty());
        requireNotInUse(room, "updated");
        RoomType roomType = requireRoomType(request.roomTypeId());
        validateRoomType(room.getProperty(), roomType);
        validateCapacity(request.capacity(), roomType);
        String roomNumber = normalizeRoomNumber(request.roomNumber());
        ensureRoomNumberAvailable(room.getProperty().getId(), roomNumber, room.getId());

        room.setRoomType(roomType);
        room.setRoomNumber(roomNumber);
        room.setFloorNumber(request.floorNumber());
        room.setCapacity(request.capacity());
        room.setDescription(normalizeOptional(request.description()));
        Room saved = rooms.save(room);
        return toResponse(saved, roomAmenities.findAmenityIdsByRoomId(saved.getId()));
    }

    public RoomResponse changeStatus(UUID id, RoomStatusChangeRequest request) {
        Room room = requireRoom(id);
        requireStatusCapability(room, request.status());
        if ((request.status() == RoomStatus.MAINTENANCE || request.status() == RoomStatus.OUT_OF_ORDER)
                && (request.reason() == null || request.reason().isBlank())) {
            throw new IllegalArgumentException("A reason is required when setting a room to maintenance or out of order");
        }
        room.setCurrentStatus(request.status());
        room.setStatusReason(normalizeOptional(request.reason()));
        room.setStatusChangedAt(OffsetDateTime.now());
        Room saved = rooms.save(room);
        return toResponse(saved, roomAmenities.findAmenityIdsByRoomId(saved.getId()));
    }

    public RoomResponse replaceAmenities(UUID id, RoomAmenitiesRequest request) {
        Room room = requireRoom(id);
        authorize("ROOM_AMENITY_UPDATE", room.getProperty());
        List<UUID> requestedIds = request.amenityIds();
        if (new HashSet<>(requestedIds).size() != requestedIds.size()) {
            throw new IllegalArgumentException("An amenity may only be assigned once to a room");
        }
        List<Amenity> requestedAmenities = amenities.findAllById(requestedIds);
        if (requestedAmenities.size() != requestedIds.size()) {
            Set<UUID> foundIds = requestedAmenities.stream().map(Amenity::getId).collect(java.util.stream.Collectors.toSet());
            UUID missingId = requestedIds.stream().filter(amenityId -> !foundIds.contains(amenityId)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Amenity", missingId);
        }
        for (Amenity amenity : requestedAmenities) {
            if (!amenity.getProperty().getId().equals(room.getProperty().getId())) {
                throw new InvalidRelationshipException("Every amenity assigned to a room must belong to the same property");
            }
        }
        roomAmenities.replaceForRoom(room.getProperty().getId(), room.getId(), requestedIds);
        return toResponse(room, requestedIds.stream().sorted().toList());
    }

    public void delete(UUID id) {
        Room room = requireRoom(id);
        authorize("ROOM_DELETE", room.getProperty());
        requireNotInUse(room, "deleted");
        rooms.delete(room);
    }

    private void requireNotInUse(Room room, String action) {
        if (room.getCurrentStatus() == RoomStatus.RESERVED || room.getCurrentStatus() == RoomStatus.OCCUPIED) {
            throw new InvalidRelationshipException("A reserved or occupied room cannot be " + action);
        }
    }

    private void requireStatusCapability(Room room, RoomStatus target) {
        if (target == RoomStatus.DIRTY || target == RoomStatus.CLEANING) {
            if (!hasCapability("ROOM_STATUS_UPDATE", room.getProperty())) {
                boolean allowedTransition = target == RoomStatus.DIRTY
                        ? room.getCurrentStatus() == RoomStatus.AVAILABLE
                        : room.getCurrentStatus() == RoomStatus.DIRTY;
                if (!allowedTransition) {
                    throw new InvalidRelationshipException("Housekeeping can only mark an available room dirty or a dirty room as cleaning");
                }
                authorization.requireCapability("ROOM_HOUSEKEEPING_STATUS_UPDATE",
                        room.getProperty().getOrganization().getId(), room.getProperty().getBranch().getId(),
                        room.getProperty().getId(), null);
            }
            return;
        }
        if (target == RoomStatus.MAINTENANCE || target == RoomStatus.OUT_OF_ORDER) {
            if (!hasCapability("ROOM_STATUS_UPDATE", room.getProperty())) {
                if (room.getCurrentStatus() == RoomStatus.RESERVED || room.getCurrentStatus() == RoomStatus.OCCUPIED) {
                    throw new InvalidRelationshipException("A reserved or occupied room cannot be taken out of service");
                }
                authorization.requireCapability("ROOM_MAINTENANCE_STATUS_UPDATE",
                        room.getProperty().getOrganization().getId(), room.getProperty().getBranch().getId(),
                        room.getProperty().getId(), null);
            }
            return;
        }
        if (target == RoomStatus.AVAILABLE) {
            if (!hasCapability("ROOM_STATUS_UPDATE", room.getProperty())) {
                if (room.getCurrentStatus() == RoomStatus.DIRTY || room.getCurrentStatus() == RoomStatus.CLEANING) {
                    authorization.requireCapability("ROOM_HOUSEKEEPING_STATUS_UPDATE",
                            room.getProperty().getOrganization().getId(), room.getProperty().getBranch().getId(),
                            room.getProperty().getId(), null);
                    return;
                }
                if (room.getCurrentStatus() == RoomStatus.MAINTENANCE || room.getCurrentStatus() == RoomStatus.OUT_OF_ORDER) {
                    authorization.requireCapability("ROOM_MAINTENANCE_STATUS_UPDATE",
                            room.getProperty().getOrganization().getId(), room.getProperty().getBranch().getId(),
                            room.getProperty().getId(), null);
                    return;
                }
                throw new AccessDeniedException("Missing capability or resource scope");
            }
            return;
        }
        authorize("ROOM_STATUS_UPDATE", room.getProperty());
    }

    private boolean hasCapability(String capability, Property property) {
        return authorization.hasCapabilityForResource(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private void validateRoomType(Property property, RoomType roomType) {
        if (!roomType.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("The room type must belong to the same property as the room");
        }
        if (roomType.getStatus() != RoomTypeStatus.ACTIVE) {
            throw new InvalidRelationshipException("An inactive room type cannot be assigned to a room");
        }
    }

    private void validateCapacity(Integer capacity, RoomType roomType) {
        if (capacity == null || capacity < 1) throw new IllegalArgumentException("Room capacity must be at least 1");
        if (capacity > roomType.getMaxOccupancy()) {
            throw new InvalidRelationshipException("Room capacity cannot exceed the room type maximum occupancy");
        }
    }

    private void ensureRoomNumberAvailable(UUID propertyId, String roomNumber, UUID excludeId) {
        boolean exists = excludeId == null
                ? rooms.existsByPropertyIdAndRoomNumberIgnoreCase(propertyId, roomNumber)
                : rooms.existsByPropertyIdAndRoomNumberIgnoreCaseAndIdNot(propertyId, roomNumber, excludeId);
        if (exists) throw new DuplicateResourceException("Room number already exists for this property");
    }

    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported room sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }

    private Room requireRoom(UUID id) {
        return rooms.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private RoomType requireRoomType(UUID id) {
        return roomTypes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type", id));
    }

    private String normalizeRoomNumber(String roomNumber) {
        return roomNumber.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private RoomResponse toResponse(Room room, List<UUID> amenityIds) {
        return new RoomResponse(room.getId(), room.getProperty().getId(), room.getProperty().getName(),
                room.getRoomType().getId(), room.getRoomType().getName(), room.getRoomNumber(), room.getFloorNumber(),
                room.getCapacity(), room.getCurrentStatus(), room.getStatusReason(), room.getStatusChangedAt(),
                room.getDescription(), amenityIds, room.getCreatedAt(), room.getUpdatedAt());
    }
}
