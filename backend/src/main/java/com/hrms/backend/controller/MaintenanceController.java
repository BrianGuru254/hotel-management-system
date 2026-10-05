package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.MaintenancePriority;
import com.hrms.backend.entity.MaintenanceStatus;
import com.hrms.backend.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/maintenance")
public class MaintenanceController {
    private final MaintenanceService service;
    public MaintenanceController(MaintenanceService service) { this.service = service; }
    @GetMapping public PagedModel<MaintenanceResponse> findAll(@RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) UUID roomId, @RequestParam(required = false) MaintenanceStatus status,
            @RequestParam(required = false) MaintenancePriority priority, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "reportedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, roomId, status, priority, page, size, sort, direction));
    }
    @GetMapping("/{id}") public MaintenanceResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @GetMapping("/{id}/history") public List<MaintenanceHistoryResponse> history(@PathVariable UUID id) { return service.history(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public MaintenanceResponse create(@Valid @RequestBody MaintenanceCreateRequest body) { return service.create(body); }
    @PatchMapping("/{id}/assign") public MaintenanceResponse assign(@PathVariable UUID id, @Valid @RequestBody MaintenanceAssignRequest body) { return service.assign(id, body); }
    @PatchMapping("/{id}/start") public MaintenanceResponse start(@PathVariable UUID id) { return service.start(id); }
    @PatchMapping("/{id}/resolve") public MaintenanceResponse resolve(@PathVariable UUID id, @Valid @RequestBody MaintenanceResolutionRequest body) { return service.resolve(id, body); }
    @PatchMapping("/{id}/close") public MaintenanceResponse close(@PathVariable UUID id) { return service.close(id); }
    @PatchMapping("/{id}/cancel") public MaintenanceResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
}
