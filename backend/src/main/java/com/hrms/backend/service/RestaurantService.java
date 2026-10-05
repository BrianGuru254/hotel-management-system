package com.hrms.backend.service;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @Transactional
public class RestaurantService {
    private static final int MAX_PAGE_SIZE = 100;
    private final RestaurantRepository restaurants;
    private final RestaurantTableRepository tables;
    private final BranchRepository branches;
    private final ScopeAuthorizationService authorization;

    public RestaurantService(RestaurantRepository restaurants, RestaurantTableRepository tables,
            BranchRepository branches, ScopeAuthorizationService authorization) {
        this.restaurants = restaurants; this.tables = tables; this.branches = branches; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<RestaurantResponse> findAll(UUID branchId, int page, int size, String direction) {
        Pageable pageable = page(page, size, "name", direction);
        ScopeVisibility scope = authorization.visibility("RESTAURANT_READ");
        Set<UUID> branchIds = visibleBranchIds(scope);
        if (branchIds.isEmpty()) return Page.empty(pageable);
        if (branchId != null && !branchIds.contains(branchId)) return Page.empty(pageable);
        return restaurants.findByBranchIdIn(branchId == null ? branchIds : Set.of(branchId), pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse findById(UUID id) { Restaurant r = requireRestaurant(id); authorize("RESTAURANT_READ", r); return response(r); }

    public RestaurantResponse create(RestaurantRequest body) {
        Branch branch = branches.findById(body.branchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", body.branchId()));
        authorize("RESTAURANT_CREATE", branch);
        String name = body.name().trim();
        if (restaurants.existsByBranchIdAndNameIgnoreCase(branch.getId(), name)) throw new DuplicateResourceException("Restaurant name already exists in this branch");
        Restaurant r = new Restaurant(); r.setBranch(branch); r.setName(name); r.setDescription(normalize(body.description()));
        r.setOpeningTime(body.openingTime()); r.setClosingTime(body.closingTime());
        if (body.active() != null) r.setActive(body.active());
        return response(restaurants.save(r));
    }

    public RestaurantResponse update(UUID id, RestaurantRequest body) {
        Restaurant r = requireRestaurant(id); authorize("RESTAURANT_UPDATE", r);
        Branch branch = branches.findById(body.branchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", body.branchId()));
        authorize("RESTAURANT_UPDATE", branch);
        String name = body.name().trim();
        if (restaurants.existsByBranchIdAndNameIgnoreCaseAndIdNot(branch.getId(), name, id)) throw new DuplicateResourceException("Restaurant name already exists in this branch");
        boolean targetActive = body.active() == null || body.active();
        if (!targetActive && r.getActive() && tables.countByRestaurantIdAndStatusNot(id, RestaurantTableStatus.AVAILABLE) > 0)
            throw new InvalidRelationshipException("A restaurant with tables in use cannot be deactivated");
        r.setBranch(branch); r.setName(name); r.setDescription(normalize(body.description()));
        r.setOpeningTime(body.openingTime()); r.setClosingTime(body.closingTime()); r.setActive(targetActive);
        return response(restaurants.save(r));
    }

    @Transactional(readOnly = true)
    public Page<RestaurantTableResponse> findTables(UUID restaurantId, int page, int size, String direction) {
        Restaurant r = requireRestaurant(restaurantId); authorize("RESTAURANT_TABLE_READ", r);
        return tables.findByRestaurantId(restaurantId, page(page, size, "tableNumber", direction)).map(this::tableResponse);
    }

    public RestaurantTableResponse createTable(UUID restaurantId, RestaurantTableRequest body) {
        Restaurant r = requireRestaurant(restaurantId); authorize("RESTAURANT_TABLE_CREATE", r);
        if (!r.getActive()) throw new InvalidRelationshipException("Tables cannot be added to an inactive restaurant");
        String number = body.tableNumber().trim();
        if (tables.existsByRestaurantIdAndTableNumberIgnoreCase(restaurantId, number)) throw new DuplicateResourceException("Table number already exists in this restaurant");
        RestaurantTable table = new RestaurantTable(); table.setRestaurant(r); table.setTableNumber(number);
        table.setCapacity(body.capacity()); table.setLocation(normalize(body.location()));
        return tableResponse(tables.save(table));
    }

    public RestaurantTableResponse updateTable(UUID id, RestaurantTableRequest body) {
        RestaurantTable table = requireTable(id); authorize("RESTAURANT_TABLE_UPDATE", table.getRestaurant());
        String number = body.tableNumber().trim();
        if (tables.existsByRestaurantIdAndTableNumberIgnoreCaseAndIdNot(table.getRestaurant().getId(), number, id))
            throw new DuplicateResourceException("Table number already exists in this restaurant");
        table.setTableNumber(number); table.setCapacity(body.capacity()); table.setLocation(normalize(body.location()));
        return tableResponse(tables.save(table));
    }

    public RestaurantTableResponse changeTableStatus(UUID id, RestaurantTableStatusRequest body) {
        RestaurantTable table = requireTable(id); authorize("RESTAURANT_TABLE_STATUS_UPDATE", table.getRestaurant());
        RestaurantTableStatus from = table.getStatus(), to = body.status();
        boolean valid = switch (from) {
            case AVAILABLE -> true;
            case RESERVED -> to == RestaurantTableStatus.OCCUPIED || to == RestaurantTableStatus.AVAILABLE;
            case OCCUPIED -> to == RestaurantTableStatus.CLEANING;
            case CLEANING -> to == RestaurantTableStatus.AVAILABLE || to == RestaurantTableStatus.OUT_OF_SERVICE;
            case OUT_OF_SERVICE -> to == RestaurantTableStatus.AVAILABLE;
        };
        if (!valid) throw new InvalidRelationshipException("Invalid restaurant table status transition: " + from + " to " + to);
        table.setStatus(to); return tableResponse(tables.save(table));
    }

    private Set<UUID> visibleBranchIds(ScopeVisibility scope) {
        if (scope.systemWide()) { Set<UUID> ids = new LinkedHashSet<>(); branches.findAll().forEach(b -> ids.add(b.getId())); return ids; }
        Set<UUID> ids = new LinkedHashSet<>(scope.branchIds());
        if (!scope.organizationIds().isEmpty()) branches.findByOrganizationIdIn(scope.organizationIds()).forEach(b -> ids.add(b.getId()));
        return ids;
    }
    private PageRequest page(int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        return PageRequest.of(page, size, Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
    }
    private Restaurant requireRestaurant(UUID id) { return restaurants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant", id)); }
    private RestaurantTable requireTable(UUID id) { return tables.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant table", id)); }
    private void authorize(String capability, Restaurant restaurant) { authorize(capability, restaurant.getBranch()); }
    private void authorize(String capability, Branch branch) { authorization.requireCapability(capability, branch.getOrganization().getId(), branch.getId(), null, null); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private RestaurantResponse response(Restaurant r) { return new RestaurantResponse(r.getId(), r.getBranch().getId(), r.getName(), r.getDescription(), r.getOpeningTime(), r.getClosingTime(), r.getActive(), r.getCreatedAt()); }
    private RestaurantTableResponse tableResponse(RestaurantTable t) { return new RestaurantTableResponse(t.getId(), t.getRestaurant().getId(), t.getTableNumber(), t.getCapacity(), t.getLocation(), t.getStatus(), t.getCreatedAt()); }
}
