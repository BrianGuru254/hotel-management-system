package com.hrms.backend.controller;

import com.hrms.backend.dto.RoomAmenitiesRequest;
import com.hrms.backend.dto.RoomCreateRequest;
import com.hrms.backend.dto.RoomResponse;
import com.hrms.backend.dto.RoomStatusChangeRequest;
import com.hrms.backend.dto.RoomUpdateRequest;
import com.hrms.backend.entity.RoomStatus;
import com.hrms.backend.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping
    public PagedModel<RoomResponse> findAll(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) UUID roomTypeId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) Integer floorNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "roomNumber") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.findAll(propertyId, roomTypeId, roomNumber, status,
                floorNumber, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody RoomCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable UUID id, @Valid @RequestBody RoomUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public RoomResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody RoomStatusChangeRequest request) {
        return service.changeStatus(id, request);
    }

    @PutMapping("/{id}/amenities")
    public RoomResponse replaceAmenities(@PathVariable UUID id, @Valid @RequestBody RoomAmenitiesRequest request) {
        return service.replaceAmenities(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
