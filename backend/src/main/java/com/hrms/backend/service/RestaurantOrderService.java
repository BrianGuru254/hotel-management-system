package com.hrms.backend.service;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service @Transactional
public class RestaurantOrderService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final List<RestaurantOrderStatus> OPEN = List.of(RestaurantOrderStatus.DRAFT, RestaurantOrderStatus.PLACED,
            RestaurantOrderStatus.CONFIRMED, RestaurantOrderStatus.PREPARING, RestaurantOrderStatus.READY, RestaurantOrderStatus.SERVED);
    private static final Set<String> SORT_FIELDS = Set.of("createdAt", "updatedAt", "total", "status");
    private final RestaurantOrderRepository orders;
    private final RestaurantOrderItemRepository orderItems;
    private final RestaurantRepository restaurants;
    private final RestaurantTableRepository tables;
    private final MenuItemRepository menuItems;
    private final GuestRepository guests;
    private final StayRepository stays;
    private final BranchRepository branches;
    private final HmsUserRepository users;
    private final ScopeAuthorizationService authorization;

    public RestaurantOrderService(RestaurantOrderRepository orders, RestaurantOrderItemRepository orderItems,
            RestaurantRepository restaurants, RestaurantTableRepository tables, MenuItemRepository menuItems,
            GuestRepository guests, StayRepository stays, BranchRepository branches, HmsUserRepository users, ScopeAuthorizationService authorization) {
        this.orders = orders; this.orderItems = orderItems; this.restaurants = restaurants; this.tables = tables;
        this.menuItems = menuItems; this.guests = guests; this.stays = stays; this.branches = branches; this.users = users; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<RestaurantOrderResponse> list(UUID restaurantId, UUID tableId, UUID guestId, UUID waiterId,
            RestaurantOrderStatus status, RestaurantOrderType type, LocalDate date, int page, int size, String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction);
        ScopeVisibility scope = authorization.visibility("ORDER_READ");
        Set<UUID> branchIds = visibleBranchIds(scope);
        if (branchIds.isEmpty()) return Page.empty(pageable);
        Set<UUID> restaurantIds = new LinkedHashSet<>();
        for (Restaurant restaurant : restaurants.findAllByBranchIdIn(branchIds)) restaurantIds.add(restaurant.getId());
        if (restaurantId != null) {
            Restaurant restaurant = requireRestaurant(restaurantId); authorize("ORDER_READ", restaurant);
            if (!restaurantIds.contains(restaurantId)) return Page.empty(pageable);
            restaurantIds = Set.of(restaurantId);
        }
        if (restaurantIds.isEmpty()) return Page.empty(pageable);
        var from = date == null ? null : date.atStartOfDay().atOffset(ZoneOffset.UTC);
        var until = date == null ? null : date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return orders.search(restaurantIds, restaurantId, tableId, guestId, waiterId, status, type, from, until, pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public RestaurantOrderResponse get(UUID id) { RestaurantOrder order = requireOrder(id); authorize("ORDER_READ", order.getRestaurant()); return response(order); }

    public RestaurantOrderResponse create(CreateOrderRequest body) {
        Restaurant restaurant = requireRestaurant(body.restaurantId()); authorize("ORDER_CREATE", restaurant);
        if (!restaurant.getActive()) throw new InvalidRelationshipException("Orders cannot be placed at an inactive restaurant");
        RestaurantTable table = null;
        if (body.tableId() != null) {
            table = requireTable(body.tableId());
            if (!table.getRestaurant().getId().equals(restaurant.getId())) throw new InvalidRelationshipException("Table belongs to another restaurant");
            if (table.getStatus() == RestaurantTableStatus.OUT_OF_SERVICE || table.getStatus() == RestaurantTableStatus.CLEANING)
                throw new InvalidRelationshipException("The selected table is not available for ordering");
        }
        if (body.orderType() == RestaurantOrderType.DINE_IN && table == null)
            throw new InvalidRelationshipException("Dine-in orders require a restaurant table");
        if (body.orderType() == RestaurantOrderType.ROOM_SERVICE && body.stayId() == null)
            throw new InvalidRelationshipException("Room-service orders require an active stay");
        Guest guest = body.guestId() == null ? null : guests.findById(body.guestId()).orElseThrow(() -> new ResourceNotFoundException("Guest", body.guestId()));
        Stay stay = body.stayId() == null ? null : stays.findById(body.stayId()).orElseThrow(() -> new ResourceNotFoundException("Stay", body.stayId()));
        if (stay != null) {
            if (stay.getStatus() != StayStatus.ACTIVE) throw new InvalidRelationshipException("Orders can only be charged to an active stay");
            if (!stay.getProperty().getBranch().getId().equals(restaurant.getBranch().getId())) throw new InvalidRelationshipException("Stay belongs to another branch");
            if (guest != null && !guest.getId().equals(stay.getGuest().getId())) throw new InvalidRelationshipException("Guest does not match the selected stay");
            if (guest == null) guest = stay.getGuest();
        }
        if (guest != null && !guest.getProperty().getBranch().getId().equals(restaurant.getBranch().getId()))
            throw new InvalidRelationshipException("Guest belongs to another branch");

        RestaurantOrder order = new RestaurantOrder(); order.setRestaurant(restaurant); order.setTable(table); order.setGuest(guest);
        order.setStay(stay); order.setWaiter(requireUser(authorization.currentUserId())); order.setOrderType(body.orderType());
        order.setNotes(normalize(body.notes())); order = orders.save(order);
        Set<UUID> distinctMenuIds = new HashSet<>();
        for (OrderItemRequest request : body.items()) {
            if (!distinctMenuIds.add(request.menuItemId())) throw new InvalidRelationshipException("Each menu item may appear only once per order; update its quantity instead");
            addLine(order, request);
        }
        recalculate(order);
        return response(order);
    }

    public RestaurantOrderResponse addItem(UUID orderId, OrderItemRequest request) {
        RestaurantOrder order = requireOrder(orderId); authorize("ORDER_UPDATE", order.getRestaurant()); requireDraft(order);
        addLine(order, request); recalculate(order); return response(order);
    }

    public RestaurantOrderResponse updateItem(UUID orderId, UUID itemId, OrderItemRequest request) {
        RestaurantOrder order = requireOrder(orderId); authorize("ORDER_UPDATE", order.getRestaurant()); requireDraft(order);
        RestaurantOrderItem line = orderItems.findByIdAndOrderId(itemId, orderId).orElseThrow(() -> new ResourceNotFoundException("Order item", itemId));
        MenuItem menu = requireMenuItem(request.menuItemId()); requireMenuAvailableForOrder(menu, order);
        line.setMenuItem(menu); snapshot(line, menu, request); orderItems.save(line); recalculate(order); return response(order);
    }

    public RestaurantOrderResponse removeItem(UUID orderId, UUID itemId) {
        RestaurantOrder order = requireOrder(orderId); authorize("ORDER_UPDATE", order.getRestaurant()); requireDraft(order);
        RestaurantOrderItem line = orderItems.findByIdAndOrderId(itemId, orderId).orElseThrow(() -> new ResourceNotFoundException("Order item", itemId));
        orderItems.delete(line); recalculate(order); return response(order);
    }

    public RestaurantOrderResponse submit(UUID id) {
        RestaurantOrder order = requireOrder(id); authorize("ORDER_UPDATE", order.getRestaurant()); requireDraft(order);
        if (orderItems.findByOrderIdOrderByCreatedAtAsc(id).isEmpty()) throw new InvalidRelationshipException("An order must contain at least one item before it is submitted");
        if (order.getTable() != null) {
            RestaurantTable table = tables.findByIdForUpdate(order.getTable().getId()).orElseThrow(() -> new ResourceNotFoundException("Restaurant table", order.getTable().getId()));
            if (!table.getRestaurant().getId().equals(order.getRestaurant().getId())) throw new InvalidRelationshipException("Table belongs to another restaurant");
            if (table.getStatus() != RestaurantTableStatus.AVAILABLE && table.getStatus() != RestaurantTableStatus.RESERVED)
                throw new InvalidRelationshipException("The selected table is already in use");
            order.setTableStatusBeforeOrder(table.getStatus()); table.setStatus(RestaurantTableStatus.OCCUPIED); tables.save(table);
        }
        order.setStatus(RestaurantOrderStatus.PLACED); return response(orders.save(order));
    }

    public RestaurantOrderResponse cancel(UUID id, OrderStatusRequest request) {
        RestaurantOrder order = requireOrder(id); authorize("ORDER_UPDATE", order.getRestaurant());
        if (order.getStatus() != RestaurantOrderStatus.DRAFT && order.getStatus() != RestaurantOrderStatus.PLACED)
            throw new InvalidRelationshipException("Only draft or placed orders can be cancelled through POS");
        if (order.getTable() != null && order.getStatus() == RestaurantOrderStatus.PLACED) {
            RestaurantTable table = tables.findByIdForUpdate(order.getTable().getId()).orElseThrow(() -> new ResourceNotFoundException("Restaurant table", order.getTable().getId()));
            if (orders.countByTableIdAndStatusIn(table.getId(), OPEN) <= 1 && table.getStatus() == RestaurantTableStatus.OCCUPIED)
                table.setStatus(order.getTableStatusBeforeOrder() == null ? RestaurantTableStatus.AVAILABLE : order.getTableStatusBeforeOrder());
            tables.save(table);
        }
        order.setStatus(RestaurantOrderStatus.CANCELLED); order.setCancellationReason(request == null ? null : normalize(request.reason()));
        return response(orders.save(order));
    }

    private void addLine(RestaurantOrder order, OrderItemRequest request) {
        MenuItem menu = requireMenuItem(request.menuItemId()); requireMenuAvailableForOrder(menu, order);
        RestaurantOrderItem line = new RestaurantOrderItem(); line.setOrder(order); line.setMenuItem(menu); snapshot(line, menu, request); orderItems.save(line);
    }
    private void snapshot(RestaurantOrderItem line, MenuItem menu, OrderItemRequest request) {
        BigDecimal subtotal = menu.getSellingPrice().multiply(BigDecimal.valueOf(request.quantity())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(menu.getTaxRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        line.setItemName(menu.getName()); line.setItemSku(menu.getSku()); line.setQuantity(request.quantity());
        line.setUnitPrice(menu.getSellingPrice()); line.setTaxRate(menu.getTaxRate()); line.setLineSubtotal(subtotal);
        line.setLineTax(tax); line.setLineTotal(subtotal.add(tax)); line.setSpecialInstructions(normalize(request.specialInstructions()));
    }
    private void recalculate(RestaurantOrder order) {
        BigDecimal subtotal = BigDecimal.ZERO, tax = BigDecimal.ZERO;
        for (RestaurantOrderItem line : orderItems.findByOrderIdOrderByCreatedAtAsc(order.getId())) { subtotal = subtotal.add(line.getLineSubtotal()); tax = tax.add(line.getLineTax()); }
        order.setSubtotal(subtotal); order.setTaxAmount(tax); order.setDiscountAmount(BigDecimal.ZERO); order.setTotal(subtotal.add(tax)); orders.save(order);
    }
    private void requireMenuAvailableForOrder(MenuItem menu, RestaurantOrder order) {
        if (!menu.getRestaurant().getId().equals(order.getRestaurant().getId())) throw new InvalidRelationshipException("Menu item belongs to another restaurant");
        if (!menu.getAvailable() || !menu.getCategory().getActive() || !menu.getRestaurant().getActive()) throw new InvalidRelationshipException("Menu item is unavailable");
    }
    private RestaurantOrderResponse response(RestaurantOrder o) {
        List<RestaurantOrderItemResponse> lineResponses = orderItems.findByOrderIdOrderByCreatedAtAsc(o.getId()).stream()
                .map(i -> new RestaurantOrderItemResponse(i.getId(), i.getMenuItem().getId(), i.getItemName(), i.getItemSku(), i.getQuantity(),
                        i.getUnitPrice(), i.getTaxRate(), i.getLineSubtotal(), i.getLineTax(), i.getLineTotal(), i.getSpecialInstructions())).toList();
        return new RestaurantOrderResponse(o.getId(), o.getRestaurant().getId(), o.getTable() == null ? null : o.getTable().getId(),
                o.getGuest() == null ? null : o.getGuest().getId(), o.getStay() == null ? null : o.getStay().getId(), o.getWaiter().getId(),
                o.getOrderType(), o.getStatus(), lineResponses, o.getSubtotal(), o.getTaxAmount(), o.getDiscountAmount(), o.getTotal(),
                o.getNotes(), o.getCreatedAt(), o.getUpdatedAt());
    }
    private Pageable pageable(int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        if (!SORT_FIELDS.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        return PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort));
    }
    private Set<UUID> visibleBranchIds(ScopeVisibility scope) {
        if (scope.systemWide()) { Set<UUID> ids = new LinkedHashSet<>(); branches.findAll().forEach(b -> ids.add(b.getId())); return ids; }
        Set<UUID> ids = new LinkedHashSet<>(scope.branchIds());
        if (!scope.organizationIds().isEmpty()) branches.findByOrganizationIdIn(scope.organizationIds()).forEach(b -> ids.add(b.getId()));
        return ids;
    }
    private Restaurant requireRestaurant(UUID id) { return restaurants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant", id)); }
    private RestaurantTable requireTable(UUID id) { return tables.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant table", id)); }
    private RestaurantOrder requireOrder(UUID id) { return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id)); }
    private MenuItem requireMenuItem(UUID id) { return menuItems.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu item", id)); }
    private HmsUser requireUser(UUID id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)); }
    private void requireDraft(RestaurantOrder o) { if (o.getStatus() != RestaurantOrderStatus.DRAFT) throw new InvalidRelationshipException("Items may only be changed on a draft order"); }
    private void authorize(String capability, Restaurant r) { authorization.requireCapability(capability, r.getBranch().getOrganization().getId(), r.getBranch().getId(), null, null); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
