package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.HousekeepingTaskStatus;
import com.hrms.backend.service.HousekeepingService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/housekeeping/tasks")
public class HousekeepingController {
    private final HousekeepingService service;
    public HousekeepingController(HousekeepingService service) { this.service = service; }
    @GetMapping public PagedModel<HousekeepingTaskResponse> findAll(@RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) UUID roomId, @RequestParam(required = false) HousekeepingTaskStatus status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, roomId, status, page, size, sort, direction));
    }
    @GetMapping("/{id}") public HousekeepingTaskResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public HousekeepingTaskResponse create(@Valid @RequestBody HousekeepingTaskCreateRequest request) { return service.create(request); }
    @PutMapping("/{id}") public HousekeepingTaskResponse update(@PathVariable UUID id, @Valid @RequestBody HousekeepingTaskUpdateRequest request) { return service.update(id, request); }
    @PostMapping("/{id}/complete") public HousekeepingTaskResponse complete(@PathVariable UUID id) { return service.complete(id); }
}
