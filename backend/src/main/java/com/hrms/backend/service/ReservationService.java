package com.hrms.backend.service;

import com.hrms.backend.dto.ReservationAvailabilityRequest;
import com.hrms.backend.dto.ReservationAvailabilityResponse;
import com.hrms.backend.dto.ReservationCancelRequest;
import com.hrms.backend.dto.ReservationCreateRequest;
import com.hrms.backend.dto.ReservationResponse;
import com.hrms.backend.dto.ReservationStatusHistoryResponse;
import com.hrms.backend.dto.ReservationUpdateRequest;
import com.hrms.backend.entity.Guest;
import com.hrms.backend.entity.HmsUser;
import com.hrms.backend.entity.Property;
import com.hrms.backend.entity.Reservation;
import com.hrms.backend.entity.ReservationStatus;
import com.hrms.backend.entity.ReservationStatusHistory;
import com.hrms.backend.entity.Room;
import com.hrms.backend.entity.RoomType;
import com.hrms.backend.entity.RoomStatus;
import com.hrms.backend.entity.StayStatus;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.GuestRepository;
import com.hrms.backend.repository.HmsUserRepository;
import com.hrms.backend.repository.PropertyRepository;
import com.hrms.backend.repository.ReservationRepository;
import com.hrms.backend.repository.ReservationStatusHistoryRepository;
import com.hrms.backend.repository.RoomRepository;
import com.hrms.backend.repository.RoomTypeRepository;
import com.hrms.backend.repository.StayRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ReservationService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "reservationNumber", "checkInDate", "checkOutDate", "status", "nightlyRate", "createdAt");
    private static final List<ReservationStatus> ACTIVE_STATUSES = List.of(
            ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN);
    private static final List<RoomStatus> UNAVAILABLE_ROOM_STATUSES = List.of(
            RoomStatus.MAINTENANCE, RoomStatus.OUT_OF_ORDER);

    private final ReservationRepository reservations;
    private final ReservationStatusHistoryRepository statusHistories;
    private final PropertyRepository properties;
    private final GuestRepository guests;
    private final RoomTypeRepository roomTypes;
    private final StayRepository stays;
    private final RoomRepository rooms;
    private final HmsUserRepository users;
    private final PropertyVisibilityService propertyVisibility;
    private final ScopeAuthorizationService authorization;

    public ReservationService(ReservationRepository reservations,
                              ReservationStatusHistoryRepository statusHistories,
                              PropertyRepository properties,
                              GuestRepository guests,
                              RoomTypeRepository roomTypes,
                              StayRepository stays,
                              RoomRepository rooms,
                              HmsUserRepository users,
                              PropertyVisibilityService propertyVisibility,
                              ScopeAuthorizationService authorization) {
        this.reservations = reservations;
        this.statusHistories = statusHistories;
        this.properties = properties;
        this.guests = guests;
        this.roomTypes = roomTypes;
        this.stays = stays;
        this.rooms = rooms;
        this.users = users;
        this.propertyVisibility = propertyVisibility;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> findAll(UUID propertyId, UUID roomTypeId, UUID guestId,
                                             ReservationStatus status, LocalDate fromDate, LocalDate toDate,
                                             String reservationNumber, int page, int size,
                                             String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility visibility = authorization.visibility("RESERVATION_READ");
        Set<UUID> visibleIds = propertyVisibility.visiblePropertyIds(visibility, propertyId);
        if (visibleIds.isEmpty()) return Page.empty(pageable);

        return reservations.searchInProperties(visibleIds, propertyId, status, guestId, fromDate, toDate,
                normalizeOptional(reservationNumber), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(UUID id) {
        Reservation reservation = requireReservation(id);
        authorize("RESERVATION_READ", reservation.getProperty());
        return toResponse(reservation);
    }

    public ReservationResponse create(ReservationCreateRequest request) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        Property property = requireProperty(request.propertyId());
        authorize("RESERVATION_CREATE", property);

        Guest guest = requireGuest(request.guestId());
        if (!guest.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("Guest does not belong to the specified property");
        }

        Room room = null;
        if (request.roomId() != null) {
            room = requireRoom(request.roomId());
            if (!room.getProperty().getId().equals(property.getId())) {
                throw new InvalidRelationshipException("Room does not belong to the specified property");
            }
        }

        RoomType roomType;
        if (request.roomTypeId() != null) {
            roomType = requireRoomType(request.roomTypeId());
            if (!roomType.getProperty().getId().equals(property.getId())) {
                throw new InvalidRelationshipException("Room type does not belong to the specified property");
            }
            if (room != null && !room.getRoomType().getId().equals(roomType.getId())) {
                throw new InvalidRelationshipException("Room does not belong to the specified room type");
            }
        } else if (room != null) {
            roomType = room.getRoomType();
        } else {
            throw new IllegalArgumentException("Either roomTypeId or roomId must be provided");
        }

        int numberOfRooms = request.numberOfRooms() == null ? 1 : request.numberOfRooms();
        if (room != null && numberOfRooms != 1) {
            throw new IllegalArgumentException("Number of rooms must be 1 when a specific room is assigned");
        }

        int adults = request.adults() == null ? 1 : request.adults();
        int children = request.children() == null ? 0 : request.children();
        int totalGuests = adults + children;
        int maxCapacity = roomType.getMaxOccupancy() * numberOfRooms;
        if (totalGuests > maxCapacity) {
            throw new InvalidRelationshipException("Total occupants (" + totalGuests + ") exceed maximum capacity ("
                    + maxCapacity + ") for the selected room(s)");
        }

        // Lock room type row to prevent race conditions during room type availability allocation
        roomTypes.findByIdForUpdate(roomType.getId());

        roomTypes.findByIdForUpdate(roomType.getId());
        long totalRoomsForType = rooms.countByPropertyIdAndRoomTypeIdAndCurrentStatusNotIn(
                property.getId(), roomType.getId(), UNAVAILABLE_ROOM_STATUSES);
        long alreadyReserved = countAllocatedRooms(property.getId(), roomType.getId(), null,
                request.checkInDate(), request.checkOutDate());
        if (totalRoomsForType - alreadyReserved < numberOfRooms) {
            throw new InvalidRelationshipException("Insufficient room availability for the requested room type and dates");
        }

        if (room != null) {
            boolean roomConflict = reservations.existsByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    room.getId(), ACTIVE_STATUSES, request.checkOutDate(), request.checkInDate());
            if (roomConflict) {
                throw new InvalidRelationshipException("The assigned room is already booked for the selected dates");
            }
        }

        BigDecimal nightlyRate = request.nightlyRate() != null ? request.nightlyRate() : roomType.getBasePrice();
        BigDecimal depositAmount = request.depositAmount() != null ? request.depositAmount() : BigDecimal.ZERO;
        String bookingSource = normalizeOptional(request.bookingSource());
        if (bookingSource == null) bookingSource = "DIRECT";

        HmsUser currentUser = getCurrentUser();
        String reservationNumber = generateReservationNumber(property.getId());

        Reservation reservation = new Reservation();
        reservation.setReservationNumber(reservationNumber);
        reservation.setProperty(property);
        reservation.setGuest(guest);
        reservation.setRoomType(roomType);
        reservation.setRoom(room);
        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setAdults(adults);
        reservation.setChildren(children);
        reservation.setNumberOfRooms(numberOfRooms);
        reservation.setNightlyRate(nightlyRate);
        reservation.setDepositAmount(depositAmount);
        reservation.setCurrencyCode(property.getCurrencyCode());
        reservation.setBookingSource(bookingSource);
        reservation.setSpecialRequests(request.specialRequests());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedBy(currentUser);

        Reservation saved = reservations.save(reservation);

        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setProperty(property);
        history.setReservation(saved);
        history.setPreviousStatus(null);
        history.setNewStatus(ReservationStatus.PENDING);
        history.setReason("Reservation created");
        history.setChangedBy(currentUser);
        statusHistories.save(history);

        return toResponse(saved);
    }

    public ReservationResponse update(UUID id, ReservationUpdateRequest request) {
        Reservation reservation = reservations.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        authorize("RESERVATION_UPDATE", reservation.getProperty());

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidRelationshipException("Only PENDING reservations can be updated. Current status: " + reservation.getStatus());
        }

        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Property property = reservation.getProperty();
        Guest guest = requireGuest(request.guestId());
        if (!guest.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("Guest does not belong to the specified property");
        }

        int numberOfRooms = request.numberOfRooms() == null ? 1 : request.numberOfRooms();
        if (reservation.getRoom() != null && numberOfRooms != 1) {
            throw new IllegalArgumentException("Number of rooms must be 1 when a specific room is assigned");
        }

        int adults = request.adults() == null ? 1 : request.adults();
        int children = request.children() == null ? 0 : request.children();
        int totalGuests = adults + children;
        int maxCapacity = reservation.getRoomType().getMaxOccupancy() * numberOfRooms;
        if (totalGuests > maxCapacity) {
            throw new InvalidRelationshipException("Total occupants (" + totalGuests + ") exceed maximum capacity ("
                    + maxCapacity + ") for the selected room(s)");
        }

        roomTypes.findByIdForUpdate(reservation.getRoomType().getId());
        long totalRoomsForType = rooms.countByPropertyIdAndRoomTypeIdAndCurrentStatusNotIn(
                property.getId(), reservation.getRoomType().getId(), UNAVAILABLE_ROOM_STATUSES);
        long alreadyReserved = countAllocatedRooms(property.getId(), reservation.getRoomType().getId(),
                reservation.getId(), request.checkInDate(), request.checkOutDate());
        if (totalRoomsForType - alreadyReserved < numberOfRooms) {
            throw new InvalidRelationshipException("Insufficient room availability for the updated dates");
        }

        if (reservation.getRoom() != null) {
            boolean roomConflict = reservations.existsByRoomIdAndIdNotAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    reservation.getRoom().getId(), reservation.getId(), ACTIVE_STATUSES, request.checkOutDate(), request.checkInDate());
            if (roomConflict) {
                throw new InvalidRelationshipException("The assigned room is already booked for the updated dates");
            }
        }

        reservation.setGuest(guest);
        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setAdults(adults);
        reservation.setChildren(children);
        reservation.setNumberOfRooms(numberOfRooms);
        if (request.nightlyRate() != null) reservation.setNightlyRate(request.nightlyRate());
        if (request.depositAmount() != null) reservation.setDepositAmount(request.depositAmount());
        if (request.bookingSource() != null) reservation.setBookingSource(request.bookingSource());
        reservation.setSpecialRequests(request.specialRequests());

        return toResponse(reservations.save(reservation));
    }

    public ReservationResponse confirm(UUID id) {
        Reservation reservation = reservations.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        authorize("RESERVATION_CONFIRM", reservation.getProperty());

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidRelationshipException("Only PENDING reservations can be confirmed. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation saved = reservations.save(reservation);

        HmsUser currentUser = getCurrentUser();
        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setProperty(reservation.getProperty());
        history.setReservation(saved);
        history.setPreviousStatus(ReservationStatus.PENDING);
        history.setNewStatus(ReservationStatus.CONFIRMED);
        history.setReason("Reservation confirmed");
        history.setChangedBy(currentUser);
        statusHistories.save(history);

        return toResponse(saved);
    }

    public ReservationResponse cancel(UUID id, ReservationCancelRequest request) {
        Reservation reservation = reservations.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        authorize("RESERVATION_CANCEL", reservation.getProperty());

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidRelationshipException("Reservation is already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED || reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new InvalidRelationshipException("Cannot cancel a reservation with status: " + reservation.getStatus());
        }

        ReservationStatus previousStatus = reservation.getStatus();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(OffsetDateTime.now());
        reservation.setCancellationReason(request.reason());
        Reservation saved = reservations.save(reservation);

        HmsUser currentUser = getCurrentUser();
        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setProperty(reservation.getProperty());
        history.setReservation(saved);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(ReservationStatus.CANCELLED);
        history.setReason(request.reason());
        history.setChangedBy(currentUser);
        statusHistories.save(history);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReservationAvailabilityResponse checkAvailability(ReservationAvailabilityRequest request) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        Property property = requireProperty(request.propertyId());
        authorize("RESERVATION_AVAILABILITY_READ", property);

        RoomType roomType = requireRoomType(request.roomTypeId());
        if (!roomType.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("Room type does not belong to the specified property");
        }

        long totalRooms = rooms.countByPropertyIdAndRoomTypeIdAndCurrentStatusNotIn(
                property.getId(), roomType.getId(), UNAVAILABLE_ROOM_STATUSES);
        long reservedRooms = countAllocatedRooms(property.getId(), roomType.getId(), null,
                request.checkInDate(), request.checkOutDate());

        int availableRooms = (int) Math.max(0, totalRooms - reservedRooms);
        int requestedRooms = request.numberOfRooms() == null ? 1 : request.numberOfRooms();
        boolean available = availableRooms >= requestedRooms;

        return new ReservationAvailabilityResponse(
                property.getId(),
                roomType.getId(),
                request.checkInDate(),
                request.checkOutDate(),
                requestedRooms,
                (int) totalRooms,
                (int) reservedRooms,
                availableRooms,
                available
        );
    }

    @Transactional(readOnly = true)
    public List<ReservationStatusHistoryResponse> findStatusHistory(UUID reservationId) {
        Reservation reservation = requireReservation(reservationId);
        authorize("RESERVATION_HISTORY_READ", reservation.getProperty());

        return statusHistories.findByPropertyIdAndReservationIdOrderByChangedAtAsc(
                reservation.getProperty().getId(), reservation.getId()).stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    private Reservation requireReservation(UUID id) {
        return reservations.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private Guest requireGuest(UUID id) {
        return guests.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }

    private RoomType requireRoomType(UUID id) {
        return roomTypes.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
    }

    private Room requireRoom(UUID id) {
        return rooms.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private long countAllocatedRooms(UUID propertyId, UUID roomTypeId, UUID excludedReservationId,
                                     LocalDate checkInDate, LocalDate checkOutDate) {
        long reservationCount = excludedReservationId == null
                ? reservations.countReservedRooms(propertyId, roomTypeId, ACTIVE_STATUSES, checkInDate, checkOutDate)
                : reservations.countReservedRoomsExcluding(propertyId, roomTypeId, excludedReservationId,
                        ACTIVE_STATUSES, checkInDate, checkOutDate);
        return reservationCount + stays.countActiveWalkInRoomsByPropertyAndRoomType(
                propertyId, roomTypeId, StayStatus.ACTIVE, checkInDate);
    }

    private HmsUser getCurrentUser() {
        try {
            UUID userId = authorization.currentUserId();
            return users.findById(userId).orElse(null);
        } catch (Exception exception) {
            return null;
        }
    }

    private String generateReservationNumber(UUID propertyId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
            if (!reservations.existsByPropertyIdAndReservationNumberIgnoreCase(propertyId, candidate)) {
                return candidate;
            }
        }
        return "RES-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
    }

    private ReservationResponse toResponse(Reservation r) {
        long nights = ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate());
        if (nights <= 0) nights = 1;
        BigDecimal estimatedTotal = r.getNightlyRate()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(r.getNumberOfRooms()));
        String guestName = r.getGuest() != null
                ? (r.getGuest().getFirstName() + " " + r.getGuest().getLastName()).trim()
                : null;
        String roomNumber = r.getRoom() != null ? r.getRoom().getRoomNumber() : null;
        UUID roomId = r.getRoom() != null ? r.getRoom().getId() : null;
        UUID createdById = r.getCreatedBy() != null ? r.getCreatedBy().getId() : null;

        return new ReservationResponse(
                r.getId(),
                r.getReservationNumber(),
                r.getProperty().getId(),
                r.getGuest().getId(),
                guestName,
                r.getRoomType().getId(),
                r.getRoomType().getName(),
                roomId,
                roomNumber,
                r.getCheckInDate(),
                r.getCheckOutDate(),
                r.getAdults(),
                r.getChildren(),
                r.getNumberOfRooms(),
                r.getNightlyRate(),
                estimatedTotal,
                r.getDepositAmount(),
                r.getCurrencyCode(),
                r.getBookingSource(),
                r.getSpecialRequests(),
                r.getStatus(),
                r.getCancelledAt(),
                r.getCancellationReason(),
                createdById,
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private ReservationStatusHistoryResponse toHistoryResponse(ReservationStatusHistory h) {
        UUID changedById = h.getChangedBy() != null ? h.getChangedBy().getId() : null;
        return new ReservationStatusHistoryResponse(
                h.getId(),
                h.getPreviousStatus(),
                h.getNewStatus(),
                h.getReason(),
                changedById,
                h.getChangedAt()
        );
    }

    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }

    private String normalizeOptional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
