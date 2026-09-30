package com.hrms.backend.controller;

import com.hrms.backend.dto.OrganizationRequest;
import com.hrms.backend.dto.OrganizationResponse;
import com.hrms.backend.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final OrganizationService service;
    public OrganizationController(OrganizationService service) { this.service = service; }

    @GetMapping public List<OrganizationResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public OrganizationResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrganizationResponse create(@Valid @RequestBody OrganizationRequest request) { return service.create(request); }
    @PutMapping("/{id}") public OrganizationResponse update(@PathVariable UUID id, @Valid @RequestBody OrganizationRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/active") public OrganizationResponse setActive(@PathVariable UUID id, @RequestParam boolean value) { return service.setActive(id, value); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
