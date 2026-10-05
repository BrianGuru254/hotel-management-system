package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1")
public class MenuController {
    private final MenuService service;
    public MenuController(MenuService service) { this.service = service; }
    @GetMapping("/restaurants/{restaurantId}/menu/categories")
    public PagedModel<MenuCategoryResponse> categories(@PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "displayOrder") String sort, @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.categories(restaurantId, page, size, sort, direction));
    }
    @PostMapping("/restaurants/{restaurantId}/menu/categories") @ResponseStatus(HttpStatus.CREATED)
    public MenuCategoryResponse createCategory(@PathVariable UUID restaurantId, @Valid @RequestBody MenuCategoryRequest request) { return service.createCategory(restaurantId, request); }
    @PutMapping("/menu/categories/{id}") public MenuCategoryResponse updateCategory(@PathVariable UUID id, @Valid @RequestBody MenuCategoryRequest request) { return service.updateCategory(id, request); }
    @DeleteMapping("/menu/categories/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCategory(@PathVariable UUID id) { service.deleteCategory(id); }

    @GetMapping("/menu/items") public PagedModel<MenuItemResponse> items(@RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) UUID categoryId, @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort, @RequestParam(defaultValue = "asc") String direction) {
        return new PagedModel<>(service.items(restaurantId, categoryId, available, page, size, sort, direction));
    }
    @GetMapping("/menu/items/{id}") public MenuItemResponse item(@PathVariable UUID id) { return service.findItem(id); }
    @PostMapping("/menu/items") @ResponseStatus(HttpStatus.CREATED) public MenuItemResponse createItem(@Valid @RequestBody MenuItemRequest request) { return service.createItem(request); }
    @PutMapping("/menu/items/{id}") public MenuItemResponse updateItem(@PathVariable UUID id, @Valid @RequestBody MenuItemRequest request) { return service.updateItem(id, request); }
    @PatchMapping("/menu/items/{id}/availability") public MenuItemResponse setAvailability(@PathVariable UUID id, @Valid @RequestBody MenuAvailabilityRequest request) { return service.setAvailability(id, request); }
    @DeleteMapping("/menu/items/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivateItem(@PathVariable UUID id) { service.deactivateItem(id); }
}
