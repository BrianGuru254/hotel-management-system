package com.hrms.backend.controller;

import com.hrms.backend.dto.PropertyRequest;
import com.hrms.backend.dto.PropertyResponse;
import com.hrms.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
    private final PropertyService service;

    public PropertyController(PropertyService service) { this.service = service; }

    @GetMapping
    public List<PropertyResponse> findAll(@RequestParam(required = false) UUID organizationId,
                                          @RequestParam(required = false) UUID branchId) {
        return service.findAll(organizationId, branchId);
    }

    @GetMapping("/{id}")
    public PropertyResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(@Valid @RequestBody PropertyRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public PropertyResponse update(@PathVariable UUID id, @Valid @RequestBody PropertyRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public PropertyResponse setActive(@PathVariable UUID id, @RequestParam boolean value) {
        return service.setActive(id, value);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
