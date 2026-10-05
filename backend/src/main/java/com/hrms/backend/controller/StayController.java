package com.hrms.backend.controller;

import com.hrms.backend.dto.StayAddGuestRequest;
import com.hrms.backend.dto.StayHistoryResponse;
import com.hrms.backend.dto.StayResponse;
import com.hrms.backend.entity.StayStatus;
import com.hrms.backend.service.StayService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stays")
public class StayController {
    private final StayService service;

    public StayController(StayService service) {
        this.service = service;
    }

    @GetMapping
    public PagedModel<StayResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) StayStatus status,
            @RequestParam(required = false) UUID guestId,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "actualCheckIn") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, status, guestId, roomId,
                checkOutFrom, checkOutTo, page, size, sort, direction));
    }

    @GetMapping("/active")
    public PagedModel<StayResponse> findActive(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "actualCheckIn") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, StayStatus.ACTIVE, null, null,
                null, null, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public StayResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/history")
    public List<StayHistoryResponse> findHistory(@PathVariable UUID id) {
        return service.findHistory(id);
    }

    @PostMapping("/{id}/guests")
    @ResponseStatus(HttpStatus.OK)
    public StayResponse addGuest(@PathVariable UUID id, @Valid @RequestBody StayAddGuestRequest request) {
        return service.addGuest(id, request);
    }
}
