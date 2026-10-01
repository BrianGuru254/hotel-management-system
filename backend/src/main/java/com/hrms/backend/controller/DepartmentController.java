package com.hrms.backend.controller;

import com.hrms.backend.dto.DepartmentRequest;
import com.hrms.backend.dto.DepartmentResponse;
import com.hrms.backend.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService service;

    public DepartmentController(DepartmentService service) { this.service = service; }

    @GetMapping
    public List<DepartmentResponse> findAll(@RequestParam(required = false) UUID propertyId) { return service.findAll(propertyId); }

    @GetMapping("/{id}")
    public DepartmentResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) { return service.update(id, request); }

    @PatchMapping("/{id}/active")
    public DepartmentResponse setActive(@PathVariable UUID id, @RequestParam boolean value) { return service.setActive(id, value); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
