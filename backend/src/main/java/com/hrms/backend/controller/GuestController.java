package com.hrms.backend.controller;

import com.hrms.backend.dto.GuestCreateRequest;
import com.hrms.backend.dto.GuestResponse;
import com.hrms.backend.dto.GuestUpdateRequest;
import com.hrms.backend.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/guests")
public class GuestController {
    private final GuestService service;

    public GuestController(GuestService service) {
        this.service = service;
    }

    @GetMapping
    public Page<GuestResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String guestNumber,
            @RequestParam(required = false) String identificationNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return service.findAll(propertyId, name, phone, email, guestNumber, identificationNumber,
                page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public GuestResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuestResponse create(@Valid @RequestBody GuestCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public GuestResponse update(@PathVariable UUID id, @Valid @RequestBody GuestUpdateRequest request) {
        return service.update(id, request);
    }
}
