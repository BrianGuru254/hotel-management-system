package com.hrms.backend.controller;

import com.hrms.backend.dto.ReservationAvailabilityRequest;
import com.hrms.backend.dto.ReservationAvailabilityResponse;
import com.hrms.backend.dto.ReservationCancelRequest;
import com.hrms.backend.dto.ReservationCreateRequest;
import com.hrms.backend.dto.ReservationResponse;
import com.hrms.backend.dto.ReservationStatusHistoryResponse;
import com.hrms.backend.dto.ReservationUpdateRequest;
import com.hrms.backend.entity.ReservationStatus;
import com.hrms.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public PagedModel<ReservationResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) UUID roomTypeId,
            @RequestParam(required = false) UUID guestId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String reservationNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, roomTypeId, guestId, status, fromDate, toDate,
                reservationNumber, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ReservationResponse update(@PathVariable UUID id, @Valid @RequestBody ReservationUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/confirm")
    public ReservationResponse confirmPatch(@PathVariable UUID id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/confirm")
    public ReservationResponse confirmPost(@PathVariable UUID id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable UUID id, @Valid @RequestBody ReservationCancelRequest request) {
        return service.cancel(id, request);
    }

    @PostMapping("/check-availability")
    public ReservationAvailabilityResponse checkAvailabilityPost(@Valid @RequestBody ReservationAvailabilityRequest request) {
        return service.checkAvailability(request);
    }

    @GetMapping("/availability")
    public ReservationAvailabilityResponse checkAvailabilityGet(
            @RequestParam UUID propertyId,
            @RequestParam UUID roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(defaultValue = "1") Integer numberOfRooms) {
        return service.checkAvailability(new ReservationAvailabilityRequest(
                propertyId, roomTypeId, checkInDate, checkOutDate, numberOfRooms));
    }

    @GetMapping("/{id}/history")
    public List<ReservationStatusHistoryResponse> findHistory(@PathVariable UUID id) {
        return service.findStatusHistory(id);
    }
}
