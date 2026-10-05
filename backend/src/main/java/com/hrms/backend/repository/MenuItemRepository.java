package com.hrms.backend.repository;

import com.hrms.backend.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    @Query(value = "select i from MenuItem i where i.restaurant.id in :restaurantIds and i.category.active = true and (:categoryId is null or i.category.id = :categoryId) and (:available is null or i.available = :available)",
        countQuery = "select count(i) from MenuItem i where i.restaurant.id in :restaurantIds and i.category.active = true and (:categoryId is null or i.category.id = :categoryId) and (:available is null or i.available = :available)")
    Page<MenuItem> search(@Param("restaurantIds") Collection<UUID> restaurantIds, @Param("categoryId") UUID categoryId,
            @Param("available") Boolean available, Pageable pageable);
    boolean existsByRestaurantIdAndSkuIgnoreCase(UUID restaurantId, String sku);
    boolean existsByRestaurantIdAndSkuIgnoreCaseAndIdNot(UUID restaurantId, String sku, UUID id);
    long countByCategoryId(UUID categoryId);
}
