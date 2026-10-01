package com.hrms.backend.controller;

import com.hrms.backend.dto.AmenityCreateRequest;
import com.hrms.backend.dto.AmenityResponse;
import com.hrms.backend.dto.AmenityUpdateRequest;
import com.hrms.backend.service.AmenityService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amenities")
public class AmenityController {
    private final AmenityService service;

    public AmenityController(AmenityService service) {
        this.service = service;
    }

    @GetMapping
    public PagedModel<AmenityResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, name, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public AmenityResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityResponse create(@Valid @RequestBody AmenityCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public AmenityResponse update(@PathVariable UUID id, @Valid @RequestBody AmenityUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
