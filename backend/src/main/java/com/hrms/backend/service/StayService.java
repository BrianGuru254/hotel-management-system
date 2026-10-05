package com.hrms.backend.service;

import com.hrms.backend.dto.CheckInRequest;
import com.hrms.backend.dto.CheckoutPreviewResponse;
import com.hrms.backend.dto.CheckoutRequest;
import com.hrms.backend.dto.StayAddGuestRequest;
import com.hrms.backend.dto.StayGuestResponse;
import com.hrms.backend.dto.StayHistoryResponse;
import com.hrms.backend.dto.StayResponse;
import com.hrms.backend.entity.Guest;
import com.hrms.backend.entity.HmsUser;
import com.hrms.backend.entity.Property;
import com.hrms.backend.entity.Reservation;
import com.hrms.backend.entity.ReservationStatus;
import com.hrms.backend.entity.ReservationStatusHistory;
import com.hrms.backend.entity.Room;
import com.hrms.backend.entity.RoomStatus;
import com.hrms.backend.entity.RoomTypeStatus;
import com.hrms.backend.entity.Stay;
import com.hrms.backend.entity.StayGuest;
import com.hrms.backend.entity.StayStatus;
import com.hrms.backend.entity.StayStatusHistory;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.GuestRepository;
import com.hrms.backend.repository.HmsUserRepository;
import com.hrms.backend.repository.PropertyRepository;
import com.hrms.backend.repository.ReservationRepository;
import com.hrms.backend.repository.ReservationStatusHistoryRepository;
import com.hrms.backend.repository.RoomRepository;
import com.hrms.backend.repository.RoomTypeRepository;
import com.hrms.backend.repository.StayGuestRepository;
import com.hrms.backend.repository.StayRepository;
import com.hrms.backend.repository.StayStatusHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class StayService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "stayNumber", "actualCheckIn", "expectedCheckOutDate", "status", "createdAt");
    private static final List<ReservationStatus> ACTIVE_RESERVATION_STATUSES = List.of(
            ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN);

    private final StayRepository stays;
    private final StayGuestRepository stayGuests;
    private final StayStatusHistoryRepository stayHistories;
    private final ReservationRepository reservations;
    private final ReservationStatusHistoryRepository reservationHistories;
    private final GuestRepository guests;
    private final RoomRepository rooms;
    private final RoomTypeRepository roomTypes;
    private final PropertyRepository properties;
    private final HmsUserRepository users;
    private final PropertyVisibilityService propertyVisibility;
    private final ScopeAuthorizationService authorization;

    public StayService(StayRepository stays, StayGuestRepository stayGuests,
                      StayStatusHistoryRepository stayHistories, ReservationRepository reservations,
                      ReservationStatusHistoryRepository reservationHistories, GuestRepository guests,
                      RoomRepository rooms, RoomTypeRepository roomTypes, PropertyRepository properties,
                      HmsUserRepository users, PropertyVisibilityService propertyVisibility,
                      ScopeAuthorizationService authorization) {
        this.stays = stays;
        this.stayGuests = stayGuests;
        this.stayHistories = stayHistories;
        this.reservations = reservations;
        this.reservationHistories = reservationHistories;
        this.guests = guests;
        this.rooms = rooms;
        this.roomTypes = roomTypes;
        this.properties = properties;
        this.users = users;
        this.propertyVisibility = propertyVisibility;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<StayResponse> findAll(UUID propertyId, StayStatus status, UUID guestId, UUID roomId,
                                      LocalDate checkOutFrom, LocalDate checkOutTo,
                                      int page, int size, String sort, String direction) {
        if (checkOutFrom != null && checkOutTo != null && checkOutTo.isBefore(checkOutFrom)) {
            throw new IllegalArgumentException("checkOutTo must be on or after checkOutFrom");
        }
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility visibility = authorization.visibility("STAY_READ");
        Set<UUID> visibleIds = propertyVisibility.visiblePropertyIds(visibility, propertyId);
        if (visibleIds.isEmpty()) return Page.empty(pageable);
        return stays.searchInProperties(visibleIds, propertyId, status, guestId, roomId,
                checkOutFrom, checkOutTo, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StayResponse findById(UUID id) {
        Stay stay = requireStay(id);
        authorize("STAY_READ", stay.getProperty());
        return toResponse(stay);
    }

    @Transactional(readOnly = true)
    public List<StayHistoryResponse> findHistory(UUID id) {
        Stay stay = requireStay(id);
        authorize("STAY_HISTORY_READ", stay.getProperty());
        return stayHistories.findByPropertyIdAndStayIdOrderByChangedAtAsc(stay.getProperty().getId(), stay.getId())
                .stream().map(this::toHistoryResponse).toList();
    }

    public StayResponse checkIn(CheckInRequest request) {
        if (request.reservationId() != null) {
            Reservation reservation = reservations.findByIdForUpdate(request.reservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.reservationId()));
            return checkInReservation(request, reservation);
        }
        return checkInWalkIn(request);
    }

    private StayResponse checkInReservation(CheckInRequest request, Reservation reservation) {
        Property property = reservation.getProperty();
        authorize("CHECK_IN", property);
        if (request.propertyId() != null && !request.propertyId().equals(property.getId())) {
            throw new InvalidRelationshipException("Property does not match the reservation property");
        }
        if (request.guestId() != null && !request.guestId().equals(reservation.getGuest().getId())) {
            throw new InvalidRelationshipException("Guest does not match the reservation guest");
        }
        if (request.expectedCheckOutDate() != null
                && !request.expectedCheckOutDate().equals(reservation.getCheckOutDate())) {
            throw new InvalidRelationshipException("Expected check-out date is taken from the reservation");
        }
        if (request.nightlyRate() != null && request.nightlyRate().compareTo(reservation.getNightlyRate()) != 0) {
            throw new InvalidRelationshipException("Nightly rate is taken from the reservation");
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED
                && reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidRelationshipException("Only CONFIRMED or partially checked-in reservations can be checked in");
        }
        if (stays.countByPropertyIdAndReservationId(property.getId(), reservation.getId())
                >= reservation.getNumberOfRooms()) {
            throw new DuplicateResourceException("Every room on this reservation has already been checked in");
        }

        LocalDate expectedCheckOutDate = reservation.getCheckOutDate();
        requireFutureCheckout(expectedCheckOutDate, property);
        Room room = selectAndLockRoom(request.roomId(), reservation.getRoom(), property);
        if (!room.getRoomType().getId().equals(reservation.getRoomType().getId())) {
            throw new InvalidRelationshipException("Room does not belong to the reservation room type");
        }
        int adults = request.adults() == null ? reservation.getAdults() : request.adults();
        int children = request.children() == null ? reservation.getChildren() : request.children();
        validateOccupancy(room, adults, children);
        ensureRoomAvailable(room, reservation.getId(), expectedCheckOutDate, property);

        HmsUser actor = currentUser();
        Stay stay = createStay(property, reservation, reservation.getGuest(), room, expectedCheckOutDate,
                adults, children, reservation.getNightlyRate(), reservation.getDepositAmount(),
                request.notes(), actor);
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            transitionReservation(reservation, ReservationStatus.CHECKED_IN, "Guest checked in", actor);
        }
        recordStayStatus(stay, null, StayStatus.ACTIVE, "Reservation check-in", actor);
        return toResponse(stay);
    }

    private StayResponse checkInWalkIn(CheckInRequest request) {
        if (request.propertyId() == null || request.guestId() == null || request.roomId() == null
                || request.expectedCheckOutDate() == null || request.nightlyRate() == null) {
            throw new IllegalArgumentException("Walk-in check-in requires propertyId, guestId, roomId, expectedCheckOutDate, and nightlyRate");
        }
        Property property = requireProperty(request.propertyId());
        authorize("WALK_IN_CHECK_IN", property);
        Guest guest = requireGuest(request.guestId());
        if (!guest.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("Guest does not belong to the specified property");
        }
        requireFutureCheckout(request.expectedCheckOutDate(), property);

        Room room = selectAndLockRoom(request.roomId(), null, property);
        int adults = request.adults() == null ? 1 : request.adults();
        int children = request.children() == null ? 0 : request.children();
        validateOccupancy(room, adults, children);
        ensureRoomAvailable(room, null, request.expectedCheckOutDate(), property);

        Stay stay = createStay(property, null, guest, room, request.expectedCheckOutDate(), adults, children,
                request.nightlyRate(), BigDecimal.ZERO, request.notes(), currentUser());
        recordStayStatus(stay, null, StayStatus.ACTIVE, "Walk-in check-in", stay.getCheckedInBy());
        return toResponse(stay);
    }

    private Stay createStay(Property property, Reservation reservation, Guest guest, Room room,
                            LocalDate expectedCheckOutDate, int adults, int children,
                            BigDecimal nightlyRate, BigDecimal depositAmount, String notes, HmsUser actor) {
        Stay stay = new Stay();
        stay.setStayNumber(generateStayNumber(property.getId()));
        stay.setProperty(property);
        stay.setReservation(reservation);
        stay.setGuest(guest);
        stay.setRoom(room);
        stay.setActiveRoomId(room.getId());
        stay.setActualCheckIn(OffsetDateTime.now());
        stay.setExpectedCheckOutDate(expectedCheckOutDate);
        stay.setAdults(adults);
        stay.setChildren(children);
        stay.setNightlyRate(nightlyRate);
        stay.setDepositAmount(depositAmount);
        stay.setCurrencyCode(property.getCurrencyCode());
        stay.setStatus(StayStatus.ACTIVE);
        stay.setNotes(normalizeOptional(notes));
        stay.setCheckedInBy(actor);
        Stay saved = stays.save(stay);

        StayGuest primaryGuest = new StayGuest();
        primaryGuest.setPropertyId(property.getId());
        primaryGuest.setStay(saved);
        primaryGuest.setGuest(guest);
        primaryGuest.setPrimaryGuest(true);
        primaryGuest.setPrimaryStayId(saved.getId());
        stayGuests.save(primaryGuest);
        room.setCurrentStatus(RoomStatus.OCCUPIED);
        room.setStatusReason(null);
        room.setStatusChangedAt(OffsetDateTime.now());
        return saved;
    }

    private Room selectAndLockRoom(UUID requestedRoomId, Room reservedRoom, Property property) {
        UUID roomId = requestedRoomId != null ? requestedRoomId : reservedRoom == null ? null : reservedRoom.getId();
        if (roomId == null) throw new IllegalArgumentException("roomId is required when the reservation has no assigned room");
        if (reservedRoom != null && !reservedRoom.getId().equals(roomId)) {
            throw new InvalidRelationshipException("The selected room does not match the room assigned to the reservation");
        }
        Room room = rooms.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        if (!room.getProperty().getId().equals(property.getId())) {
            throw new InvalidRelationshipException("Room does not belong to the specified property");
        }
        roomTypes.findByIdForUpdate(room.getRoomType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room type", room.getRoomType().getId()));
        if (room.getRoomType().getStatus() != RoomTypeStatus.ACTIVE) {
            throw new InvalidRelationshipException("A room with an inactive room type cannot be checked in");
        }
        return room;
    }

    private void ensureRoomAvailable(Room room, UUID excludeReservationId,
                                     LocalDate expectedCheckOutDate, Property property) {
        if (room.getCurrentStatus() != RoomStatus.AVAILABLE) {
            throw new InvalidRelationshipException("The selected room is not available for check-in");
        }
        LocalDate today = LocalDate.now(zoneId(property));
        if (stays.existsByRoomIdAndStatus(room.getId(), StayStatus.ACTIVE)) {
            throw new DuplicateResourceException("The selected room already has an active stay");
        }
        boolean conflictingReservation = excludeReservationId == null
                ? reservations.existsByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        room.getId(), ACTIVE_RESERVATION_STATUSES, expectedCheckOutDate, today)
                : reservations.existsByRoomIdAndIdNotAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        room.getId(), excludeReservationId, ACTIVE_RESERVATION_STATUSES, expectedCheckOutDate, today);
        if (conflictingReservation) {
            throw new InvalidRelationshipException("The selected room has another reservation during the stay dates");
        }
    }

    private void validateOccupancy(Room room, int adults, int children) {
        if (adults < 1 || children < 0) throw new IllegalArgumentException("At least one adult is required and children cannot be negative");
        if ((long) adults + children > room.getCapacity()) {
            throw new InvalidRelationshipException("Guest count exceeds the selected room capacity");
        }
    }

    private void requireFutureCheckout(LocalDate checkOutDate, Property property) {
        if (checkOutDate == null || !checkOutDate.isAfter(LocalDate.now(zoneId(property)))) {
            throw new InvalidRelationshipException("Expected check-out date must be after the property's current date");
        }
    }

    public StayResponse addGuest(UUID stayId, StayAddGuestRequest request) {
        Stay stay = stays.findByIdForUpdate(stayId)
                .orElseThrow(() -> new ResourceNotFoundException("Stay", stayId));
        authorize("STAY_GUEST_MANAGE", stay.getProperty());
        if (stay.getStatus() != StayStatus.ACTIVE) {
            throw new InvalidRelationshipException("Guests can only be added to an active stay");
        }
        Guest guest = requireGuest(request.guestId());
        if (!guest.getProperty().getId().equals(stay.getProperty().getId())) {
            throw new InvalidRelationshipException("Guest does not belong to the stay property");
        }
        var existingAssignment = stayGuests.findByStayIdAndGuestId(stay.getId(), guest.getId());
        if (existingAssignment.isPresent()) {
            if (!request.isPrimaryGuest() || Boolean.TRUE.equals(existingAssignment.get().getPrimaryGuest())) {
                throw new DuplicateResourceException("Guest is already assigned to this stay");
            }
            setPrimaryGuest(stay, guest, existingAssignment.get());
            return toResponse(stay);
        }
        if (stayGuests.countByStayId(stay.getId()) >= stay.getRoom().getCapacity()) {
            throw new InvalidRelationshipException("The guest list cannot exceed the room capacity");
        }
        if (Boolean.TRUE.equals(request.isPrimaryGuest())) {
            clearPrimaryGuest(stay);
            stay.setGuest(guest);
        }
        StayGuest stayGuest = new StayGuest();
        stayGuest.setPropertyId(stay.getProperty().getId());
        stayGuest.setStay(stay);
        stayGuest.setGuest(guest);
        stayGuest.setPrimaryGuest(request.isPrimaryGuest());
        stayGuest.setPrimaryStayId(request.isPrimaryGuest() ? stay.getId() : null);
        stayGuests.save(stayGuest);
        return toResponse(stay);
    }

    private void setPrimaryGuest(Stay stay, Guest guest, StayGuest assignment) {
        clearPrimaryGuest(stay);
        assignment.setPrimaryGuest(true);
        assignment.setPrimaryStayId(stay.getId());
        stay.setGuest(guest);
        stayGuests.save(assignment);
    }

    private void clearPrimaryGuest(Stay stay) {
        List<StayGuest> currentPrimary = stayGuests.findByStayIdAndPrimaryGuestTrue(stay.getId());
        currentPrimary.forEach(existing -> {
            existing.setPrimaryGuest(false);
            existing.setPrimaryStayId(null);
        });
        if (!currentPrimary.isEmpty()) stayGuests.flush();
    }

    @Transactional(readOnly = true)
    public CheckoutPreviewResponse checkoutPreview(UUID stayId) {
        Stay stay = requireStay(stayId);
        authorize("CHECKOUT_PREVIEW_READ", stay.getProperty());
        LocalDate actualCheckInDate = stay.getActualCheckIn().atZoneSameInstant(zoneId(stay.getProperty())).toLocalDate();
        long nights = Math.max(1, ChronoUnit.DAYS.between(actualCheckInDate, stay.getExpectedCheckOutDate()));
        BigDecimal accommodation = stay.getNightlyRate().multiply(BigDecimal.valueOf(nights));
        BigDecimal balance = accommodation.subtract(stay.getDepositAmount()).max(BigDecimal.ZERO);
        return new CheckoutPreviewResponse(stay.getId(), stay.getStayNumber(), nights, stay.getNightlyRate(),
                accommodation, stay.getDepositAmount(), balance, stay.getCurrencyCode());
    }

    public StayResponse checkOut(UUID stayId, CheckoutRequest request) {
        Stay stay = stays.findByIdForUpdate(stayId)
                .orElseThrow(() -> new ResourceNotFoundException("Stay", stayId));
        authorize("CHECK_OUT", stay.getProperty());
        if (stay.getStatus() != StayStatus.ACTIVE) {
            throw new InvalidRelationshipException("Only ACTIVE stays can be checked out");
        }
        HmsUser actor = currentUser();
        Room room = rooms.findByIdForUpdate(stay.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", stay.getRoom().getId()));
        if (room.getCurrentStatus() != RoomStatus.OCCUPIED) {
            throw new InvalidRelationshipException("The room status does not match an active stay");
        }

        StayStatus previousStatus = stay.getStatus();
        OffsetDateTime now = OffsetDateTime.now();
        stay.setStatus(StayStatus.CHECKED_OUT);
        stay.setActiveRoomId(null);
        stay.setActualCheckOut(now);
        stay.setCheckedOutBy(actor);
        stay.setCheckoutNotes(request == null ? null : normalizeOptional(request.notes()));
        Stay saved = stays.save(stay);

        room.setCurrentStatus(RoomStatus.DIRTY);
        room.setStatusReason("Guest checked out");
        room.setStatusChangedAt(now);
        recordStayStatus(saved, previousStatus, StayStatus.CHECKED_OUT, "Guest checked out", actor);
        boolean completeReservation = request != null && request.completeReservation();
        completeReservationIfNoActiveStays(saved.getReservation(), actor, completeReservation);
        return toResponse(saved);
    }

    private void completeReservationIfNoActiveStays(Reservation stayReservation, HmsUser actor,
                                                    boolean completeUnusedRooms) {
        if (stayReservation == null) return;
        Reservation reservation = reservations.findByIdForUpdate(stayReservation.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", stayReservation.getId()));
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) return;
        long activeStayCount = stays.countByReservationIdAndStatus(reservation.getId(), StayStatus.ACTIVE);
        if (activeStayCount != 0) {
            if (completeUnusedRooms) {
                throw new InvalidRelationshipException("A reservation cannot be finalized while another room is still checked in");
            }
            return;
        }
        long checkedInRoomCount = stays.countByPropertyIdAndReservationId(
                reservation.getProperty().getId(), reservation.getId());
        if (!completeUnusedRooms && checkedInRoomCount < reservation.getNumberOfRooms()) return;
        transitionReservation(reservation, ReservationStatus.COMPLETED, "All checked-in rooms have checked out", actor);
    }

    private void transitionReservation(Reservation reservation, ReservationStatus target, String reason, HmsUser actor) {
        ReservationStatus previous = reservation.getStatus();
        reservation.setStatus(target);
        Reservation saved = reservations.save(reservation);
        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setProperty(reservation.getProperty());
        history.setReservation(saved);
        history.setPreviousStatus(previous);
        history.setNewStatus(target);
        history.setReason(reason);
        history.setChangedBy(actor);
        reservationHistories.save(history);
    }

    private void recordStayStatus(Stay stay, StayStatus previous, StayStatus target, String reason, HmsUser actor) {
        StayStatusHistory history = new StayStatusHistory();
        history.setPropertyId(stay.getProperty().getId());
        history.setStay(stay);
        history.setPreviousStatus(previous);
        history.setNewStatus(target);
        history.setReason(reason);
        history.setChangedBy(actor);
        stayHistories.save(history);
    }

    private Stay requireStay(UUID id) {
        return stays.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stay", id));
    }

    private Property requireProperty(UUID id) {
        return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property", id));
    }

    private Guest requireGuest(UUID id) {
        return guests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Guest", id));
    }

    private HmsUser currentUser() {
        UUID id = authorization.currentUserId();
        return users.findById(id).filter(HmsUser::getActive)
                .orElseThrow(() -> new AccessDeniedException("The authenticated user is inactive or no longer exists"));
    }

    private void authorize(String capability, Property property) {
        authorization.requireCapability(capability, property.getOrganization().getId(),
                property.getBranch().getId(), property.getId(), null);
    }

    private ZoneId zoneId(Property property) {
        try {
            return ZoneId.of(property.getTimezone());
        } catch (DateTimeException exception) {
            throw new InvalidRelationshipException("Property has an invalid time zone configuration");
        }
    }

    private String generateStayNumber(UUID propertyId) {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = "STY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
            if (!stays.existsByPropertyIdAndStayNumberIgnoreCase(propertyId, candidate)) return candidate;
        }
        return "STY-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
    }

    private StayResponse toResponse(Stay stay) {
        Guest primaryGuest = stay.getGuest();
        UUID reservationId = stay.getReservation() == null ? null : stay.getReservation().getId();
        UUID checkedInBy = stay.getCheckedInBy() == null ? null : stay.getCheckedInBy().getId();
        UUID checkedOutBy = stay.getCheckedOutBy() == null ? null : stay.getCheckedOutBy().getId();
        List<StayGuestResponse> guestResponses = stayGuests
                .findByPropertyIdAndStayIdOrderByCreatedAtAsc(stay.getProperty().getId(), stay.getId())
                .stream().map(this::toGuestResponse).toList();
        return new StayResponse(stay.getId(), stay.getStayNumber(), stay.getProperty().getId(), reservationId,
                primaryGuest.getId(), guestName(primaryGuest), stay.getRoom().getId(), stay.getRoom().getRoomNumber(),
                stay.getActualCheckIn(), stay.getExpectedCheckOutDate(), stay.getActualCheckOut(), stay.getAdults(),
                stay.getChildren(), stay.getNightlyRate(), stay.getDepositAmount(), stay.getCurrencyCode(),
                stay.getStatus(), stay.getNotes(), stay.getCheckoutNotes(), guestResponses, checkedInBy,
                checkedOutBy, stay.getCreatedAt(), stay.getUpdatedAt());
    }

    private StayGuestResponse toGuestResponse(StayGuest stayGuest) {
        return new StayGuestResponse(stayGuest.getGuest().getId(), guestName(stayGuest.getGuest()),
                Boolean.TRUE.equals(stayGuest.getPrimaryGuest()), stayGuest.getCreatedAt());
    }

    private StayHistoryResponse toHistoryResponse(StayStatusHistory history) {
        return new StayHistoryResponse(history.getId(), history.getPreviousStatus(), history.getNewStatus(),
                history.getReason(), history.getChangedBy().getId(), history.getChangedAt());
    }

    private String guestName(Guest guest) {
        return (guest.getFirstName() + " " + guest.getLastName()).trim();
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater");
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported stay sort field");
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
