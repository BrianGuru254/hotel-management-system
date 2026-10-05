package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.RestaurantOrderStatus;
import com.hrms.backend.entity.RestaurantOrderType;
import com.hrms.backend.service.RestaurantOrderService;
import jakarta.validation.Valid;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/orders")
public class RestaurantOrderController {
    private final RestaurantOrderService service;
    public RestaurantOrderController(RestaurantOrderService service) { this.service = service; }

    @GetMapping public PagedModel<RestaurantOrderResponse> list(@RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) UUID tableId, @RequestParam(required = false) UUID guestId,
            @RequestParam(required = false) UUID waiterId, @RequestParam(required = false) RestaurantOrderStatus status,
            @RequestParam(required = false) RestaurantOrderType orderType, @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        return new PagedModel<>(service.list(restaurantId, tableId, guestId, waiterId, status, orderType, date, page, size, sort, direction));
    }
    @GetMapping("/{id}") public RestaurantOrderResponse get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RestaurantOrderResponse create(@Valid @RequestBody CreateOrderRequest request) { return service.create(request); }
    @PostMapping("/{id}/items") @ResponseStatus(HttpStatus.CREATED)
    public RestaurantOrderResponse addItem(@PathVariable UUID id, @Valid @RequestBody OrderItemRequest request) { return service.addItem(id, request); }
    @PutMapping("/{id}/items/{itemId}")
    public RestaurantOrderResponse updateItem(@PathVariable UUID id, @PathVariable UUID itemId, @Valid @RequestBody OrderItemRequest request) { return service.updateItem(id, itemId, request); }
    @DeleteMapping("/{id}/items/{itemId}")
    public RestaurantOrderResponse removeItem(@PathVariable UUID id, @PathVariable UUID itemId) { return service.removeItem(id, itemId); }
    @PostMapping("/{id}/submit") public RestaurantOrderResponse submit(@PathVariable UUID id) { return service.submit(id); }
    @PostMapping("/{id}/cancel") public RestaurantOrderResponse cancel(@PathVariable UUID id, @RequestBody(required = false) OrderStatusRequest request) { return service.cancel(id, request); }
}
