package com.hrms.backend.service;

import com.hrms.backend.dto.GuestHistoryResponse;
import com.hrms.backend.entity.Guest;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.GuestRepository;
import com.hrms.backend.repository.ReservationRepository;
import com.hrms.backend.repository.StayRepository;
import com.hrms.backend.repository.StayGuestRepository;
import com.hrms.backend.entity.Stay;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;

@Service
public class GuestHistoryService {
    private static final int HISTORY_LIMIT = 100;
    private final GuestRepository guests;
    private final ReservationRepository reservations;
    private final StayRepository stays;
    private final StayGuestRepository stayGuests;
    private final ScopeAuthorizationService authorization;

    public GuestHistoryService(GuestRepository guests, ReservationRepository reservations,
            StayRepository stays, StayGuestRepository stayGuests, ScopeAuthorizationService authorization) {
        this.guests = guests; this.reservations = reservations; this.stays = stays;
        this.stayGuests = stayGuests; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public GuestHistoryResponse get(UUID guestId) {
        Guest guest = guests.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("Guest", guestId));
        var property = guest.getProperty();
        authorization.requireCapability("GUEST_READ", property.getOrganization().getId(), property.getBranch().getId(), property.getId(), null);
        var pageableReservations = PageRequest.of(0, HISTORY_LIMIT, Sort.by(Sort.Direction.DESC, "checkInDate"));
        var pageableStays = PageRequest.of(0, HISTORY_LIMIT, Sort.by(Sort.Direction.DESC, "actualCheckIn"));
        List<GuestHistoryResponse.ReservationRecord> reservationRows = reservations
                .findByPropertyIdAndGuestId(property.getId(), guestId, pageableReservations).stream()
                .map(r -> new GuestHistoryResponse.ReservationRecord(r.getId(), r.getReservationNumber(),
                        r.getCheckInDate(), r.getCheckOutDate(), r.getNumberOfRooms(), r.getStatus())).toList();
        Map<UUID, Stay> guestStays = new LinkedHashMap<>();
        stays.findByPropertyIdAndGuestId(property.getId(), guestId, pageableStays)
                .forEach(s -> guestStays.put(s.getId(), s));
        stayGuests.findByPropertyIdAndGuestId(property.getId(), guestId, pageableStays)
                .forEach(link -> guestStays.put(link.getStay().getId(), link.getStay()));
        List<GuestHistoryResponse.StayRecord> stayRows = guestStays.values().stream()
                .sorted(Comparator.comparing(Stay::getActualCheckIn).reversed()).limit(HISTORY_LIMIT)
                .map(s -> new GuestHistoryResponse.StayRecord(s.getId(), s.getStayNumber(), s.getRoom().getRoomNumber(),
                        s.getActualCheckIn(), s.getActualCheckOut(), s.getStatus())).toList();
        return new GuestHistoryResponse(guestId, property.getId(), reservationRows, stayRows,
                List.of("reservations", "stays"), List.of("orders", "invoices", "payments", "loyalty"));
    }
}
