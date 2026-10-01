package com.hrms.backend.controller;

import com.hrms.backend.dto.RoomTypeCreateRequest;
import com.hrms.backend.dto.RoomTypeResponse;
import com.hrms.backend.dto.RoomTypeUpdateRequest;
import com.hrms.backend.entity.RoomTypeStatus;
import com.hrms.backend.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/room-types")
public class RoomTypeController {
    private final RoomTypeService service;

    public RoomTypeController(RoomTypeService service) {
        this.service = service;
    }

    @GetMapping
    public PagedModel<RoomTypeResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) RoomTypeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, name, code, status, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public RoomTypeResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTypeResponse create(@Valid @RequestBody RoomTypeCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public RoomTypeResponse update(@PathVariable UUID id, @Valid @RequestBody RoomTypeUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
