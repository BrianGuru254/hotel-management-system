package com.hrms.backend.service;

import com.hrms.backend.dto.*;
import com.hrms.backend.entity.*;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.exception.ResourceNotFoundException;
import com.hrms.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service @Transactional
public class MenuService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> CATEGORY_SORTS = Set.of("name", "displayOrder", "createdAt");
    private static final Set<String> ITEM_SORTS = Set.of("name", "sku", "sellingPrice", "createdAt");
    private final MenuCategoryRepository categories;
    private final MenuItemRepository items;
    private final RestaurantRepository restaurants;
    private final BranchRepository branches;
    private final ScopeAuthorizationService authorization;

    public MenuService(MenuCategoryRepository categories, MenuItemRepository items, RestaurantRepository restaurants,
            BranchRepository branches, ScopeAuthorizationService authorization) {
        this.categories = categories; this.items = items; this.restaurants = restaurants; this.branches = branches; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<MenuCategoryResponse> categories(UUID restaurantId, int page, int size, String sort, String direction) {
        Restaurant r = requireRestaurant(restaurantId); authorize("MENU_READ", r);
        return categories.findByRestaurantIdAndActiveTrue(restaurantId, pageable(page, size, sort, direction, CATEGORY_SORTS)).map(this::categoryResponse);
    }

    public MenuCategoryResponse createCategory(UUID restaurantId, MenuCategoryRequest body) {
        Restaurant r = requireRestaurant(restaurantId); authorize("MENU_CREATE", r);
        if (!r.getActive()) throw new InvalidRelationshipException("Menu categories cannot be added to an inactive restaurant");
        String name = body.name().trim();
        if (categories.existsByRestaurantIdAndNameIgnoreCase(restaurantId, name)) throw new DuplicateResourceException("Category name already exists in this restaurant");
        MenuCategory c = new MenuCategory(); c.setRestaurant(r); c.setName(name); c.setDescription(normalize(body.description()));
        c.setDisplayOrder(body.displayOrder() == null ? 0 : body.displayOrder());
        if (body.active() != null) c.setActive(body.active());
        return categoryResponse(categories.save(c));
    }

    public MenuCategoryResponse updateCategory(UUID id, MenuCategoryRequest body) {
        MenuCategory c = requireCategory(id); authorize("MENU_UPDATE", c.getRestaurant());
        String name = body.name().trim();
        if (categories.existsByRestaurantIdAndNameIgnoreCaseAndIdNot(c.getRestaurant().getId(), name, id))
            throw new DuplicateResourceException("Category name already exists in this restaurant");
        if (Boolean.TRUE.equals(body.active()) && !c.getRestaurant().getActive())
            throw new InvalidRelationshipException("A category cannot be activated under an inactive restaurant");
        c.setName(name); c.setDescription(normalize(body.description()));
        if (body.displayOrder() != null) c.setDisplayOrder(body.displayOrder());
        if (body.active() != null) c.setActive(body.active());
        return categoryResponse(categories.save(c));
    }

    public void deleteCategory(UUID id) {
        MenuCategory c = requireCategory(id); authorize("MENU_UPDATE", c.getRestaurant());
        c.setActive(false); categories.save(c);
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> items(UUID restaurantId, UUID categoryId, Boolean available,
            int page, int size, String sort, String direction) {
        Pageable pageable = pageable(page, size, sort, direction, ITEM_SORTS);
        ScopeVisibility scope = authorization.visibility("MENU_READ");
        Set<UUID> branchIds = visibleBranchIds(scope);
        if (branchIds.isEmpty()) return Page.empty(pageable);
        Set<UUID> restaurantIds = new LinkedHashSet<>();
        for (Restaurant restaurant : restaurants.findAllByBranchIdIn(branchIds)) restaurantIds.add(restaurant.getId());
        if (restaurantId != null) {
            Restaurant selected = requireRestaurant(restaurantId);
            authorize("MENU_READ", selected);
            if (!restaurantIds.contains(restaurantId)) return Page.empty(pageable);
            restaurantIds = Set.of(restaurantId);
        }
        if (restaurantIds.isEmpty()) return Page.empty(pageable);
        return items.search(restaurantIds, categoryId, available, pageable).map(this::itemResponse);
    }

    @Transactional(readOnly = true)
    public MenuItemResponse findItem(UUID id) { MenuItem item = requireItem(id); authorize("MENU_READ", item.getRestaurant()); return itemResponse(item); }

    public MenuItemResponse createItem(MenuItemRequest body) {
        MenuCategory category = requireCategory(body.categoryId()); Restaurant restaurant = category.getRestaurant();
        authorize("MENU_CREATE", restaurant);
        if (!restaurant.getActive() || !category.getActive()) throw new InvalidRelationshipException("Menu items require an active restaurant and category");
        String sku = normalizeSku(body.sku());
        if (items.existsByRestaurantIdAndSkuIgnoreCase(restaurant.getId(), sku)) throw new DuplicateResourceException("Menu item SKU already exists in this restaurant");
        MenuItem item = new MenuItem(); item.setRestaurant(restaurant); item.setCategory(category); apply(item, body, sku);
        item.setAvailable(true);
        return itemResponse(items.save(item));
    }

    public MenuItemResponse updateItem(UUID id, MenuItemRequest body) {
        MenuItem item = requireItem(id); authorize("MENU_UPDATE", item.getRestaurant());
        MenuCategory category = requireCategory(body.categoryId());
        if (!category.getRestaurant().getId().equals(item.getRestaurant().getId())) throw new InvalidRelationshipException("Category belongs to another restaurant");
        if (!category.getActive()) throw new InvalidRelationshipException("Menu items must belong to an active category");
        String sku = normalizeSku(body.sku());
        if (items.existsByRestaurantIdAndSkuIgnoreCaseAndIdNot(item.getRestaurant().getId(), sku, id)) throw new DuplicateResourceException("Menu item SKU already exists in this restaurant");
        item.setCategory(category); apply(item, body, sku);
        return itemResponse(items.save(item));
    }

    public MenuItemResponse setAvailability(UUID id, MenuAvailabilityRequest body) {
        MenuItem item = requireItem(id); authorize("MENU_UPDATE", item.getRestaurant());
        if (body.available() && (!item.getRestaurant().getActive() || !item.getCategory().getActive()))
            throw new InvalidRelationshipException("An item is available only when its restaurant and category are active");
        item.setAvailable(body.available()); return itemResponse(items.save(item));
    }

    public void deactivateItem(UUID id) {
        MenuItem item = requireItem(id); authorize("MENU_UPDATE", item.getRestaurant());
        item.setAvailable(false); items.save(item);
    }

    private void apply(MenuItem item, MenuItemRequest body, String sku) {
        item.setName(body.name().trim()); item.setSku(sku); item.setDescription(normalize(body.description()));
        item.setSellingPrice(body.sellingPrice()); item.setTaxRate(body.taxRate() == null ? BigDecimal.ZERO : body.taxRate());
        item.setPreparationTimeMinutes(body.preparationTimeMinutes() == null ? 0 : body.preparationTimeMinutes());
    }
    private Set<UUID> visibleBranchIds(ScopeVisibility scope) {
        if (scope.systemWide()) { Set<UUID> ids = new LinkedHashSet<>(); branches.findAll().forEach(b -> ids.add(b.getId())); return ids; }
        Set<UUID> ids = new LinkedHashSet<>(scope.branchIds());
        if (!scope.organizationIds().isEmpty()) branches.findByOrganizationIdIn(scope.organizationIds()).forEach(b -> ids.add(b.getId()));
        return ids;
    }
    private Pageable pageable(int page, int size, String sort, String direction, Set<String> fields) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        if (!fields.contains(sort)) throw new IllegalArgumentException("Unsupported sort field");
        return PageRequest.of(page, size, Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort));
    }
    private Restaurant requireRestaurant(UUID id) { return restaurants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant", id)); }
    private MenuCategory requireCategory(UUID id) { return categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu category", id)); }
    private MenuItem requireItem(UUID id) { return items.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu item", id)); }
    private void authorize(String capability, Restaurant r) { authorization.requireCapability(capability, r.getBranch().getOrganization().getId(), r.getBranch().getId(), null, null); }
    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String normalizeSku(String value) { return value.trim().toUpperCase(Locale.ROOT); }
    private MenuCategoryResponse categoryResponse(MenuCategory c) { return new MenuCategoryResponse(c.getId(), c.getRestaurant().getId(), c.getName(), c.getDescription(), c.getDisplayOrder(), c.getActive(), c.getCreatedAt()); }
    private MenuItemResponse itemResponse(MenuItem i) { return new MenuItemResponse(i.getId(), i.getRestaurant().getId(), i.getCategory().getId(), i.getCategory().getName(), i.getName(), i.getSku(), i.getDescription(), i.getSellingPrice(), i.getTaxRate(), i.getPreparationTimeMinutes(), i.getAvailable() && i.getRestaurant().getActive() && i.getCategory().getActive(), i.getCreatedAt()); }
}
