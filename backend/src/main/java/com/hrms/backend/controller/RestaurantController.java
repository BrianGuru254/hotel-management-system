package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1")
public class RestaurantController {
    private final RestaurantService service;
    public RestaurantController(RestaurantService service) { this.service = service; }
    @GetMapping("/restaurants") public PagedModel<RestaurantResponse> findAll(@RequestParam(required = false) UUID branchId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.findAll(branchId, page, size, direction));
    }
    @GetMapping("/restaurants/{id}") public RestaurantResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @PostMapping("/restaurants") @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) { return service.create(request); }
    @PutMapping("/restaurants/{id}") public RestaurantResponse update(@PathVariable UUID id, @Valid @RequestBody RestaurantRequest request) { return service.update(id, request); }
    @GetMapping("/restaurants/{restaurantId}/tables")
    public PagedModel<RestaurantTableResponse> findTables(@PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String direction) { return new PagedModel<>(service.findTables(restaurantId, page, size, direction)); }
    @PostMapping("/restaurants/{restaurantId}/tables") @ResponseStatus(HttpStatus.CREATED)
    public RestaurantTableResponse createTable(@PathVariable UUID restaurantId, @Valid @RequestBody RestaurantTableRequest request) { return service.createTable(restaurantId, request); }
    @PutMapping("/tables/{id}") public RestaurantTableResponse updateTable(@PathVariable UUID id, @Valid @RequestBody RestaurantTableRequest request) { return service.updateTable(id, request); }
    @PatchMapping("/tables/{id}/status")
    public RestaurantTableResponse changeTableStatus(@PathVariable UUID id, @Valid @RequestBody RestaurantTableStatusRequest request) { return service.changeTableStatus(id, request); }
}
